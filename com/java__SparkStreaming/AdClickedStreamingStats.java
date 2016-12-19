package com.java__SparkStreaming;

import com.google.common.base.Optional;
import groovy.lang.Tuple;
import kafka.serializer.StringDecoder;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.api.java.function.Function2;
import org.apache.spark.api.java.function.PairFunction;
import org.apache.spark.api.java.function.VoidFunction;
import org.apache.spark.sql.DataFrame;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.RowFactory;
import org.apache.spark.sql.hive.HiveContext;
import org.apache.spark.sql.types.DataTypes;
import org.apache.spark.sql.types.StructField;
import org.apache.spark.sql.types.StructType;
import org.apache.spark.streaming.Durations;
import org.apache.spark.streaming.api.java.JavaDStream;
import org.apache.spark.streaming.api.java.JavaPairDStream;
import org.apache.spark.streaming.api.java.JavaPairInputDStream;
import org.apache.spark.streaming.api.java.JavaStreamingContext;
import org.apache.spark.streaming.kafka.KafkaUtils;
import org.kitesdk.shaded.com.google.common.base.*;
import scala.Int;
import scala.Tuple2;

import java.sql.*;
import java.util.*;
import java.util.Objects;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by Administrator on 2016/7/1.
 * 数据格式：timestamp,ip,userID,adID,province,city
 */
public class AdClickedStreamingStats {

    public static void mian(String[] args) {

        SparkConf conf = new SparkConf().setMaster("local[5]").setAppName("AdClickedStreamingStats");
        JavaStreamingContext jsc = new JavaStreamingContext(conf, Durations.seconds(10));

        Map<String,String> kafkaParameters = new HashMap<String,String>();
        kafkaParameters.put("metadata.broker.list", "master:9092,slave1:9092,slave2:9092");
        Set<String> topics = new HashSet<String>();
        topics.add("SparkStreamingDirected");
        JavaPairInputDStream<String,String> adClickedStreaming = KafkaUtils.createDirectStream(jsc,
                String.class,
                String.class,
                StringDecoder.class,
                StringDecoder.class,
                kafkaParameters,
                topics);
        /**
         *整体思路
         * 统计过滤出有效点击 adClickedStreaming ->mapToPair->reduceByKey->filter->foreachRDD（存储到外部存储系统中）
         * 黑名单过滤：一天中同一个用户点击同一个ad超过50次就列入黑名单
         *             数据每次流入的时候首先需要判断是否是黑名单 adClickedStreaming.transformToPair（查询黑名单数据表
         *             blacklisttable 然后进行过滤）
         *             然后后面进行黑名单的生成（一天中同一个用户点击（adclicked）同一个ad超过50次就列入黑名单），然后
         *             写入数据库blacklisttable以供下次数据流入进来进行判断过滤出黑名单
         */
        //基本数据格式：timestamp、ip、userID、adID、province、

        /**
         * 因为要对黑名单进行在线过滤，而数据是在RDD中的，所以必然使用transform，
         * 但是在这里我们必须使用transformToPair，原因是读取进来的Kafka的数据是<String, Long>类型的
         * 另外一个原因是过滤后的数据要进行进一步处理。所以必须是读进来的Kafka数据的原始类型DStream<String,Long>
         */
        JavaPairDStream<String,String> filteradClickedStreaming = adClickedStreaming.transformToPair(new Function<JavaPairRDD<String, String>, JavaPairRDD<String, String>>() {
            @Override
            public JavaPairRDD<String, String> call(JavaPairRDD<String, String> rdd) throws Exception {
                /**
                 * 思路：
                 * 1，从数据库中获取黑名单转换成RDD，即用新的RDD实例封装黑名单数据
                 * 2，然后把代表黑名单的RDD的实例和Batch Duration产生的RDD进行leftOuterJoin操作。也就是说使用Batch Duration
                 * 产生的RDD和代表黑名单的RDD的实例进行leftOuterJoin操作，如果两者都有内容
                 * 的话，就会是true，否则的话就是false；
                 * 我们要留下来的是操作结果是false
                 */
                JavaSparkContext jsc = new JavaSparkContext(rdd.context());
                /**
                 * 黑名单的表中只有userID，但是如果要进行join操作的话，就必须是key-value，
                 * 所以在这里我们需要基于数据表中的数据产生Key-Value类型的数据集合；
                 */
                List<String> blackListNames = new ArrayList<String>();//用于保存从数据库查询出来的黑名单
                JDBCWrapper jdbcWrapper = JDBCWrapper.getJDBCInstance();
                jdbcWrapper.doQueryBatch("SELECT * FROM blacklisttable", null, new ExecuteCallBack() {
                    @Override
                    public void resultCallBack(ResultSet result) throws Exception {
                        while(result.next()){
                            blackListNames.add(result.getString(1));
                        }
                    }
                });
                List<Tuple2<String,Boolean>> blackListTuple = new ArrayList<Tuple2<String, Boolean>>();
                for(String name : blackListNames) {
                    blackListTuple.add(new Tuple2<String,Boolean>(name,true));//映射成name，true
                }
                //List<Tuple2<String,Boolean>>
                List blackListFromDB =blackListTuple;//数据来自于查询的黑名单数据表中并映射成String，Boolean。
                JavaPairRDD<String, Boolean> blackListRDD = jsc.parallelizePairs(blackListFromDB);

                /**
                 * 进行join操作的时候肯定是基于userID进行join的，所以必须把rdd进行mapToPair操作转化成为符合格式的rdd
                 */
                JavaPairRDD<String, Tuple2<String, String>> rdd2Pair = rdd.mapToPair(new PairFunction<Tuple2<String, String>, String, Tuple2<String, String>>() {
                    @Override
                    public Tuple2<String, Tuple2<String, String>> call(Tuple2<String, String> t) throws Exception {
                        String userID = t._2().split("\t")[2];
                        return new Tuple2<String, Tuple2<String, String>>(userID, t);
                    }
                });
                /**
                 * var rdd1 = sc.makeRDD(Array(("A","1"),("B","2"),("C","3")),2)
                 var rdd2 = sc.makeRDD(Array(("A","a"),("C","c"),("D","d")),2)

                 scala> rdd1.leftOuterJoin(rdd2).collect
                 res11: Array[(String, (String, Option[String]))] = Array((B,(2,None)), (A,(1,Some(a))), (C,(3,Some(c))))
                 */
                JavaPairRDD<String, Tuple2<Tuple2<String, String>, Optional<Boolean>>> joined = rdd2Pair.leftOuterJoin(blackListRDD);

                JavaPairRDD<String, String> result = joined.filter(new Function<Tuple2<String, Tuple2<Tuple2<String, String>, Optional<Boolean>>>, Boolean>() {
                    @Override
                    public Boolean call(Tuple2<String, Tuple2<Tuple2<String, String>, Optional<Boolean>>> v1) throws Exception {
                        Optional<Boolean> optional = v1._2()._2();
                        if (optional.isPresent() && optional.get()) {
                            return false;
                        } else {
                            return true;
                        }
                    }
                }).mapToPair(new PairFunction<Tuple2<String, Tuple2<Tuple2<String, String>, Optional<Boolean>>>, String, String>() {
                    @Override
                    public Tuple2<String, String> call(Tuple2<String, Tuple2<Tuple2<String, String>, Optional<Boolean>>> t) throws Exception {
                        return t._2()._1();
                    }
                });
                return result;
            }
        });
        JavaPairDStream<String,Long> pairs = filteradClickedStreaming.mapToPair(new PairFunction<Tuple2<String, String>, String, Long>() {
            @Override
            public Tuple2<String, Long> call(Tuple2<String, String> t) throws Exception {
                String[] splited = t._2().split("\t");
                String timestamp = splited[0];//YYYY-MM-DD
                String ip = splited[1];
                String userID = splited[2];
                String adID = splited[3];
                String province = splited[4];
                String city = splited[5];
                String clickedRecord = timestamp + "_" +
                        ip + "_" +
                        userID + "_" +
                        adID + "_" +
                        province + "_" +
                        city;
                return new Tuple2<String, Long>(clickedRecord, 1L);
            }
        });

        //计算每个Batch Duration用户广告点击量
        JavaPairDStream<String,Long>  adClickedUsers= pairs.reduceByKey(new Function2<Long, Long, Long>() {
            @Override
            public Long call(Long v1, Long v2) throws Exception {
                return v1 + v2;
            }
        });
        /**
         * 计算出有效的点击
         * 此处是判断每个Batch Duration中用户点击的次数如果大于一次,则过滤掉
         */
        JavaPairDStream<String,Long> filteredClickInBatch = adClickedUsers.filter(new Function<Tuple2<String, Long>, Boolean>() {
            @Override
            public Boolean call(Tuple2<String, Long> v1) throws Exception {
                if(v1._2() > 1) {
                    //更新一下黑名单的数据表
                    return false;
                } else {
                    return true;
                }
            }
        });

        /**
         * 默认情况下，RDD中的数据插入MySQL中是一条一条的插入的，也就是说遍历每个Partition的iterator中的每一条记录，每一次
         * 都要建立一个数据库的链接，当我们使用foreachRDD的时候操作的对象是RD，然后我们使用rdd的foreachPartition，此时操作
         * 的对象是RDD，而不是一条一条的记录，也就是说每次读取的是整个Partition。读取数据的时候效率非常高，然后我们采用
         * ExecuteBatch的方法插入或者更新数据，此时也是数据库更加高效的链接和更新方式。不过一次读取一个Partition的弊端是
         * 有可能内存OOM，所以此时就需要非常关注内存的使用。
         */
        filteredClickInBatch.foreachRDD(new Function<JavaPairRDD<String, Long>, Void>() {
            @Override
            public Void call(JavaPairRDD<String, Long> rdd) throws Exception {
                rdd.foreachPartition(new VoidFunction<Iterator<Tuple2<String, Long>>>() {
                    @Override
                    public void call(Iterator<Tuple2<String, Long>> partition) throws Exception {
                        /**
                         * 在这里我们使用数据库连接池的高效读写数据库的方式把数据写入数据库MySQL；
                         * 由于传入的参数是一个Iterator类型的集合，所以为了更加高效的操作我们需要
                         * 批量处理（数据库的操作），例如说一次性插入1000条Record，使用insertBatch或者
                         * updateBatch类型的操作；插入的用户信息可以只包含：timestamp,ip,user,adID,province,city
                         * 这里有一个问题：可能出现两条记录的Key是一样的，因此就需要更新累加操作
                         */

                        List<UserAdClicked> userAdClickedList = new ArrayList<UserAdClicked>();//保存插入数据库的数据

                        while(partition.hasNext()) {
                            Tuple2<String,Long> record = partition.next();
                            String[] splited = record._1().split("\t");

                            UserAdClicked userClicked = new UserAdClicked();
                            userClicked.setTimestamp(splited[0]);
                            userClicked.setIp(splited[1]);
                            userClicked.setUserID(splited[2]);
                            userClicked.setAdID(splited[3]);
                            userClicked.setProvince(splited[4]);
                            userClicked.setCity(splited[5]);
                            userAdClickedList.add(userClicked);
                        }
                        List<UserAdClicked> inserting = new ArrayList<UserAdClicked>();//保存需要插入的
                        List<UserAdClicked> updating = new ArrayList<UserAdClicked>();//保存需要更新的

                        JDBCWrapper jdbcWrapper = JDBCWrapper.getJDBCInstance();
                        //adclicked 表的字段：timestamp,ip,user,adID,province,city,clickedCount
                        for(UserAdClicked clicked : userAdClickedList) {
                            jdbcWrapper.doQueryBatch("SELECT count(1) FROM adclicked WHERE "
                                    + "timestamp = ? AND userID = ? AND adID = ? ", new Object[]{
                                    clicked.getTimestamp(), clicked.getUserID(), clicked.getAdID()}, new ExecuteCallBack() {
                                @Override
                                public void resultCallBack(ResultSet result) throws Exception {
                                    if(result.next()){
                                        long count = result.getLong(1);
                                        clicked.setClickedCount(count);
                                        updating.add(clicked);
                                    } else {
                                        inserting.add(clicked);
                                    }
                                }
                            });
                        }

                        //插入操作
                        ArrayList<Object[]> insertParametersList = new ArrayList<Object[]>();
                        for(UserAdClicked insertRecord: inserting){
                            insertParametersList.add(new Object[]{
                                    insertRecord.getTimestamp(),
                                    insertRecord.getIp(),
                                    insertRecord.getUserID(),
                                    insertRecord.getAdID(),
                                    insertRecord.getProvince(),
                                    insertRecord.getCity(),
                                    insertRecord.getClickedCount()
                            });
                        }
                        jdbcWrapper.doBatch("INSERT INTO adclicked VALUES(?,?,?,?,?,?,?",insertParametersList);

                        //更新操作
                        ArrayList<Object[]> updateParametersList = new ArrayList<Object[]>();
                        for(UserAdClicked updateRecord: updating){
                            updateParametersList.add(new Object[]{
                                    updateRecord.getClickedCount(),
                                    updateRecord.getTimestamp(),
                                    updateRecord.getIp(),
                                    updateRecord.getUserID(),
                                    updateRecord.getAdID(),
                                    updateRecord.getProvince(),
                                    updateRecord.getCity()
                            });
                        }
                        jdbcWrapper.doBatch("UPDATE adclicked set clickedCount = ? " +
                                "  WHERE timestamp = ? AND ip = ? AND userID = ? AND adID = ? " +
                                " AND province = ? AND city = ? ",updateParametersList);

                    }
                });
                return null;
            }
        });

        //二次过滤，通过判断用户当天累计点击广告的次数是否大于某个阈值（例如50），用户当天点击某个广告的次数在数据表中
        JavaPairDStream<String,Long> blackListBasedOnHistory = filteredClickInBatch.filter(new Function<Tuple2<String, Long>, Boolean>() {
            @Override
            public Boolean call(Tuple2<String, Long> v1) throws Exception {
                String[] splited = v1._1().split("\t");

                String data = splited[0];
                String userID = splited[2];
                String adId = splited[3];

                /**
                 * 接下来根据data、userID、adID等条件去查询用户点击广告的数据表，
                 * 获得总的点击次数，这个时候基于点击次数判断是否属于黑名单点击。
                 */
                int clickedCountTotalToday = 81;
                if(clickedCountTotalToday > 50) {
                    return true;//黑名单
                } else {
                    return false;
                }
            }
        });

        //下一步把blackListBasedOnHistory写入到黑名单数据表中
        //filteredClickInBatch.foreachRDD由于其内部使用 rdd.foreachPartition，所以有可能partition之间存在同一用户，用户重复，
        //所以需要整个RDD去重
        //以上是人家的意思，我认为不对，因为前面进行了reduceByKey，我认为是这个样子：
        // 因为前面的Key是由userID和adID共同决定的，所以可以出现同一用户点击不同的广告
        /**
         * 必须对黑名单的整个RDD进行去重操作
         * 怎么对其（blackListBasedOnHistory）进行去重操作，方法：transform直接对rdd进行操作，使用rdd.distinct，原因就是
         * DStream没有该方法，但是RDD含有该方法。
         */
        JavaDStream<String> blackListuserIDBasedOnHistory = blackListBasedOnHistory.map(new Function<Tuple2<String, Long>, String>() {
            @Override
            public String call(Tuple2<String, Long> v1) throws Exception {
                return v1._1().split("]t")[2];
            }
        });
        JavaDStream<String> blackListUniqueuserIDBasedOnHistory = blackListuserIDBasedOnHistory.transform(new Function<JavaRDD<String>, JavaRDD<String>>() {
            @Override
            public JavaRDD<String> call(JavaRDD<String> rdd) throws Exception {
                return rdd.distinct();
            }
        });
        //下一步写入黑名单数据表中（结束）
        blackListUniqueuserIDBasedOnHistory.foreachRDD(new Function<JavaRDD<String>, Void>() {
            @Override
            public Void call(JavaRDD<String> rdd) throws Exception {
                rdd.foreachPartition(new VoidFunction<Iterator<String>>() {
                    @Override
                    public void call(Iterator<String> t) throws Exception {
                        /**
                         * 在这里我们使用数据库连接池的高效读写数据库的方式把数据写入数据库MySQL；
                         * 由于传入的参数是一个Iterator类型的集合，所以为了更加高效的操作我们需要
                         * 批量处理（数据库的操作），例如说一次性插入1000条Record，使用insertBatch或者
                         * updateBatch类型的操作；插入的用户信息可以只包含：userID\
                         */
                        List<Object[]> blackList = new ArrayList<Object[]>();

                        while(t.hasNext()) {
                            blackList.add(new Object[]{(Object)t.next()});
                        }
                        JDBCWrapper jdbcWrapper =  JDBCWrapper.getJDBCInstance();
                        jdbcWrapper.doBatch("INSERT INTO blacklisttable VALUES (?)",blackList);

                    }
                });
                return null;
            }
        });


        /**
         * 广告点击累计次数动态更新（多个Batch之间），每个updateStateByKey都会在 Batch Duration的时间
         * 间隔的基础上进行广告点击次数的更新，更新之后我们一般都会持久化到外部存储设备上，在这里我们存储到
         * MySQL数据库中；
         */
        JavaPairDStream<String,Long> updateStateByKeyDStream = filteradClickedStreaming.mapToPair(new PairFunction<Tuple2<String, String>, String, Long>() {
            @Override
            public Tuple2<String, Long> call(Tuple2<String, String> t) throws Exception {
                String[] splited = t._2().split("\t");
                String timestamp = splited[0];//YYYY-MM-DD
                String adID = splited[3];
                String province = splited[4];
                String city = splited[5];

                String clickedRecord = timestamp + "_" + adID + "_" + province + "_" + city;
                return new Tuple2<String, Long>(clickedRecord, 1L);
            }
        }).updateStateByKey(new Function2<List<Long>, Optional<Long>, Optional<Long>>() {
            @Override
            public Optional<Long> call(List<Long> v1, Optional<Long> v2) throws Exception {
                /**
                 * v1:代表的是当前的key在当前的Batch Duration中出现的次数的集合。例如{1,1,1}
                 * v2:代表当前key在以前的Batch Duration中积累下来的结果；
                 */
                Long clickedTotalHistory = 0L;
                if (v2.isPresent()) {
                    clickedTotalHistory = v2.get();
                }
                for (Long one : v1) {
                    clickedTotalHistory += one;
                }
                return Optional.of(clickedTotalHistory);
            }
        });
        updateStateByKeyDStream.foreachRDD(new Function<JavaPairRDD<String, Long>, Void>() {
            @Override
            public Void call(JavaPairRDD<String, Long> rdd) throws Exception {
                rdd.foreachPartition(new VoidFunction<Iterator<Tuple2<String, Long>>>() {
                    @Override
                    public void call(Iterator<Tuple2<String, Long>> partition) throws Exception {
                        List<AdClicked> adClickedList = new ArrayList<AdClicked>();

                        while (partition.hasNext()) {
                            Tuple2<String, Long> record = partition.next();
                            String[] splited = record._1().split("\t");

                            AdClicked adClicked = new AdClicked();
                            adClicked.setTimestamp(splited[0]);
                            adClicked.setAdID(splited[1]);
                            adClicked.setProvince(splited[2]);
                            adClicked.setCity(splited[3]);
                            adClicked.setClickedCount(record._2());

                            adClickedList.add(adClicked);
                        }

                        List<AdClicked> inserting = new ArrayList<AdClicked>();//保存需要插入的
                        List<AdClicked> updating = new ArrayList<AdClicked>();//保存需要更新的

                        JDBCWrapper jdbcWrapper = JDBCWrapper.getJDBCInstance();
                        for (AdClicked clicked : adClickedList) {
                            jdbcWrapper.doQueryBatch("SELECT count(1) FROM adclickedcount WHERE "
                                    + "timestamp = ? AND userID = ? AND adID = ?,And province = ? AND city = ? ", new Object[]{
                                    clicked.getTimestamp(), clicked.getTimestamp(), clicked.getAdID(), clicked.getProvince(), clicked.getCity()}, new ExecuteCallBack() {
                                @Override
                                public void resultCallBack(ResultSet result) throws Exception {
                                    if (result.next()) {
                                        long count = result.getLong(1);
                                        clicked.setClickedCount(count);
                                        updating.add(clicked);
                                    } else {
                                        inserting.add(clicked);
                                    }
                                }
                            });
                        }

                        //插入操作
                        ArrayList<Object[]> insertParametersList = new ArrayList<Object[]>();
                        for (AdClicked insertRecord : inserting) {
                            insertParametersList.add(new Object[]{
                                    insertRecord.getTimestamp(),
                                    insertRecord.getAdID(),
                                    insertRecord.getProvince(),
                                    insertRecord.getCity(),
                                    insertRecord.getClickedCount()
                            });
                        }
                        jdbcWrapper.doBatch("INSERT INTO adclickedcount VALUES(?,?,?,?,?", insertParametersList);

                        //更新操作
                        ArrayList<Object[]> updateParametersList = new ArrayList<Object[]>();
                        for (AdClicked updateRecord : updating) {
                            updateParametersList.add(new Object[]{
                                    updateRecord.getClickedCount(),
                                    updateRecord.getTimestamp(),
                                    updateRecord.getAdID(),
                                    updateRecord.getProvince(),
                                    updateRecord.getCity()
                            });
                        }
                        jdbcWrapper.doBatch("UPDATE adclickedcount set clickedCount = ? " +
                                "  WHERE timestamp = ? AND adID = ? AND province = ? AND city = ?", updateParametersList);

                    }
                });
                return null;
            }
        });

        /**
         * 对广告点击进行TopN的计算，计算出每天每个省份的Top5排名的广告
         * 因为要对RDD进行操作，所以我们使用了transform算子
         * 最后保存到数据库中(先删除后插入)
         */
        updateStateByKeyDStream.transform(new Function<JavaPairRDD<String, Long>, JavaRDD<Row>>() {
            @Override
            public JavaRDD<Row> call(JavaPairRDD<String, Long> rdd) throws Exception {
                JavaRDD<Row> rowRDD = rdd.mapToPair(new PairFunction<Tuple2<String,Long>, String, Long>() {
                    @Override
                    public Tuple2<String, Long> call(Tuple2<String, Long> t) throws Exception {
                        String[] splited = t._1().split("_");
                        String timestamp = splited[0];
                        String adID = splited[1];
                        String province = splited[2];

                        String clickedRecord = timestamp + "_" + adID + "_" + province;
                        return new Tuple2<String, Long>(clickedRecord,t._2());
                    }
                }).reduceByKey(new Function2<Long, Long, Long>() {
                    @Override
                    public Long call(Long v1, Long v2) throws Exception {
                        return v1 + v2;
                    }
                }).map(new Function<Tuple2<String,Long>, Row>() {
                    @Override
                    public Row call(Tuple2<String, Long> v1) throws Exception {
                        String[] splited = v1._1().split("_");
                        String timestamp = splited[0];
                        String adID = splited[1];
                        String province = splited[2];
                        return RowFactory.create(timestamp,adID,province,v1._2());
                    }
                });

                StructType structType = DataTypes.createStructType(Arrays.asList(
                        DataTypes.createStructField("timestamp", DataTypes.StringType, true),
                        DataTypes.createStructField("adID", DataTypes.StringType, true),
                        DataTypes.createStructField("province", DataTypes.StringType, true),
                        DataTypes.createStructField("clickedCount", DataTypes.StringType, true)
                ));
                HiveContext hiveContext = new HiveContext(rdd.context());
                DataFrame df = hiveContext.createDataFrame(rowRDD,structType);
                df.registerTempTable("topNTableSource");
                DataFrame result = hiveContext.sql("SELECT timestamp,adID,province,clickedCount FROM" +
                        "(SELECT timestamp,adID,province,clickedCount,ROW_NUMBER OVER(PARTITION BY " +
                        " province ORDER BY clickedCount DESC) rank FROM topNTableSource) subquery " +
                        "WHERE rank <= 5 " );


                return result.toJavaRDD();
            }
        }).foreach(new Function<JavaRDD<Row>, Void>() {
            @Override
            public Void call(JavaRDD<Row> rdd) throws Exception {
                rdd.foreachPartition(new VoidFunction<Iterator<Row>>() {
                    @Override
                    public void call(Iterator<Row> t) throws Exception {
                        List<AdProvinceTopN> adProvinceTopN = new ArrayList<AdProvinceTopN>();
                        while (t.hasNext()) {
                            Row row = t.next();
                            AdProvinceTopN item = new AdProvinceTopN();
                            item.setTimestamp(row.getString(0));
                            item.setAdID(row.getString(1));
                            item.setProvince(row.getString(2));
                            item.setClickedCount(row.getString(3));
                            adProvinceTopN.add(item);
                        }

                        JDBCWrapper jdbcWrapper = JDBCWrapper.getJDBCInstance();

                        //去重操作 原因就在于delete删除操作的where条件，他是根据province删除的，topn里面一个地方有5个广告
                        Set<String> set = new HashSet<String>();
                        for (AdProvinceTopN item : adProvinceTopN) {
                            set.add(item.getTimestamp() + "_" + item.getProvince());
                        }

                        ArrayList<Object[]> deleteParametersList = new ArrayList<Object[]>();
                        for (String deleteRecord : set) {
                            String[] splited = deleteRecord.split("_");
                            deleteParametersList.add(new Object[]{
                                    splited[0], splited[1]
                            });
                        }
                        jdbcWrapper.doBatch("DELETE FROM adprovincetopn WHERE timestamp = ? AND province = ?", deleteParametersList);

                        //插入操作
                        //adprovincetopn 表的子酸：timestamp,adIN,province,clickedCount
                        ArrayList<Object[]> insertParametersList = new ArrayList<Object[]>();
                        for (AdProvinceTopN updateRecord : adProvinceTopN) {
                            insertParametersList.add(new Object[]{
                                    updateRecord.getTimestamp(),
                                    updateRecord.getAdID(),
                                    updateRecord.getProvince(),
                                    updateRecord.getProvince()
                            });
                        }
                        jdbcWrapper.doBatch("INSERT INTO adprovincetopn VALUES(?,?,?,?)", insertParametersList);

                    }
                });
                return null;
            }
        });

        /**
         * 分析30分钟广告的点击趋势
         * 然后把数据放到DB中，然后通过第三方绘制趋势图
         * filteradClickedStreaming里面是kafka里面获取的数据，key没有意义，value是需要的数据
         */
        filteradClickedStreaming.mapToPair(new PairFunction<Tuple2<String,String>, String, Long>() {
            @Override
            public Tuple2<String, Long> call(Tuple2<String, String> t) throws Exception {
                String[] splited = t._2().split("\t");
                String adID = splited[3];
                String time = splited[0];//Todo:后续需要重构代码实现时间戳和分钟的转换提取，此处需要提取出该广告的分钟单位
                return new Tuple2<String, Long>(time + "_" + adID,1L);
            }
        }).reduceByKeyAndWindow(new Function2<Long, Long, Long>() {
            @Override
            public Long call(Long v1, Long v2) throws Exception {
                return v1 + v2;
            }
        }, new Function2<Long, Long, Long>() {
            @Override
            public Long call(Long v1, Long v2) throws Exception {
                return v1 - v2;
            }
        }, Durations.minutes(30), Durations.minutes(5)).foreachRDD(new Function<JavaPairRDD<String, Long>, Void>() {
            @Override
            public Void call(JavaPairRDD<String, Long> rdd) throws Exception {
                rdd.foreachPartition(new VoidFunction<Iterator<Tuple2<String, Long>>>() {
                    @Override
                    public void call(Iterator<Tuple2<String, Long>> partition) throws Exception {
                        List<AdTrendStat> adTrend = new ArrayList<AdTrendStat>();
                        while(partition.hasNext()){
                            Tuple2<String,Long> record = partition.next();
                            String[] splited = record._1().split("_");
                            String time = splited[0];
                            String adID = splited[1];
                            Long ClickedCount = record._2();

                            /**
                             * 在插入数据到数据库的时候具体需要哪些字段？time、adID、ClickedCount
                             * 而我们通过J2EE技术进行趋势绘图的时候肯定是需要年、月、日、时、分这个维度的，所以
                             * 我们在里面需要年、月、日、时、分这些时间维度；
                             */

                            AdTrendStat adtrendStat = new AdTrendStat();
                            adtrendStat.setAdID(adID);
                            adtrendStat.setClickedCount(ClickedCount);
                            adtrendStat.set_data(time);//Todo:获取年月日
                            adtrendStat.set_hour(time);//Todo:获取小时
                            adtrendStat.set_minute(time);//TOdo获取分钟

                            adTrend.add(adtrendStat);
                        }

                        List<AdTrendStat> inserting = new ArrayList<AdTrendStat>();//保存需要插入的
                        List<AdTrendStat> updating = new ArrayList<AdTrendStat>();//保存需要更新的

                        JDBCWrapper jdbcWrapper = JDBCWrapper.getJDBCInstance();
                        for (AdTrendStat clicked : adTrend) {
                            AdTrendCountHistory adTrendCountHistory = new AdTrendCountHistory();
                            jdbcWrapper.doQueryBatch("SELECT count(1) FROM adclickedtrend WHERE "
                                    + "date = ? AND hour = ? AND minute = ?,And adID = ?  ", new Object[]{
                                    clicked.get_data(), clicked.get_hour(), clicked.get_minute(), clicked.getAdID()}, new ExecuteCallBack() {
                                @Override
                                public void resultCallBack(ResultSet result) throws Exception {
                                    if (result.next()) {
                                        long count = result.getLong(1);
                                        adTrendCountHistory.setClickedCountHistory(count);
                                        updating.add(clicked);
                                    } else {
                                        inserting.add(clicked);
                                    }
                                }
                            });
                        }

                        //插入操作
                        ArrayList<Object[]> insertParametersList = new ArrayList<Object[]>();
                        for (AdTrendStat insertRecord : inserting) {
                            insertParametersList.add(new Object[]{
                                    insertRecord.get_data(),
                                    insertRecord.get_hour(),
                                    insertRecord.get_minute(),
                                    insertRecord.getClickedCount(),
                            });
                        }
                        //adclickedtrend字段：data,minute,adID,clickedCount
                        jdbcWrapper.doBatch("INSERT INTO adclickedtrend VALUES(?,?,?,?", insertParametersList);

                        //更新操作:因为以分钟为单位的，而数据流入是10秒钟，所以有可能是1分钟有相同的广告有许多次
                        ArrayList<Object[]> updateParametersList = new ArrayList<Object[]>();
                        for (AdTrendStat updateRecord : updating) {
                            updateParametersList.add(new Object[]{
                                    updateRecord.getClickedCount(),
                                    updateRecord.get_data(),
                                    updateRecord.get_hour(),
                                    updateRecord.get_minute(),
                                    updateRecord.getAdID()

                            });
                        }
                        jdbcWrapper.doBatch("UPDATE adclickedtrend set clickedCount = ? " +
                                "  WHERE date = ? AND hour = ? AND minute = ?,And adID = ?", updateParametersList);
                    }
                });
                return null;
            }
        });
        jsc.start();
        jsc.awaitTermination();
        jsc.close();
    }
}
class JDBCWrapper {

    private static JDBCWrapper jdbcInstance = null;
    private static LinkedBlockingQueue<Connection> dbConnectionPool = new LinkedBlockingQueue<Connection>();

    static {
        try {
            Class.forName("com.mysql.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public  static JDBCWrapper getJDBCInstance() {
        if(jdbcInstance == null) {
            synchronized (JDBCWrapper.class) {
                if(jdbcInstance == null) {
                    jdbcInstance = new JDBCWrapper();
                }
            }
        }
        return jdbcInstance;
    }

    private JDBCWrapper() {
        for (int i = 0; i < 10; i++) {
            try {
                Connection conn = DriverManager.getConnection("jdbc://mysql://master:3306/sparkstreaming", "root", "root");
                dbConnectionPool.put(conn);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public synchronized  Connection getConnection(){
        while (0 == dbConnectionPool.size()) {
            try {
                Thread.sleep(20);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return dbConnectionPool.poll();
    }

    public int[] doBatch(String sqlText,List<Object[]> paramsList) {
        Connection conn = getConnection();
        PreparedStatement preparedStatement = null;
        int[] result = null;
        try {
            conn.setAutoCommit(false);
            preparedStatement = conn.prepareStatement(sqlText);
            for(Object[] parameters : paramsList) {
                for(int i = 0; i < parameters.length; i++) {
                    preparedStatement.setObject(i+1,parameters[i]);
                }
                preparedStatement.addBatch();
            }
            result = preparedStatement.executeBatch();
            conn.commit();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if(preparedStatement != null) {
                try {
                    preparedStatement.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if(conn != null) {
                try {
                    dbConnectionPool.put(conn);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
        return result;
    }

    public void doQueryBatch(String sqlText,Object[] paramsList,ExecuteCallBack callBack) {
        Connection conn = getConnection();
        PreparedStatement preparedStatement = null;
        ResultSet result = null;
        try {
            preparedStatement = conn.prepareStatement(sqlText);
                for(int i = 0; i < paramsList.length; i++) {
                    preparedStatement.setObject(i+1,paramsList[i]);
                }
            result = preparedStatement.executeQuery();
            try {
                callBack.resultCallBack(result);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if(preparedStatement != null) {
                try {
                    preparedStatement.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if(conn != null) {
                try {
                    dbConnectionPool.put(conn);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}

//doBatchQuery方法的回调函数的接口
interface ExecuteCallBack {
    void resultCallBack(ResultSet result) throws Exception;
}

/**
 * javaBean:对adclicked表的字段进行封装
 */
class UserAdClicked{
    private String timestamp;
    private String ip;
    private String userID;
    private String adID;
    private String province;
    private String city;

    public Long getClickedCount() {
        return clickedCount;
    }

    public void setClickedCount(Long clickedCount) {
        this.clickedCount = clickedCount;
    }

    private Long clickedCount;




    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getAdID() {
        return adID;
    }

    public void setAdID(String adID) {
        this.adID = adID;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

}

class AdClicked {
    private String timestamp;
    private String adID;
    private String province;
    private String city;
    private Long clickedCount;

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getAdID() {
        return adID;
    }

    public void setAdID(String adID) {
        this.adID = adID;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public Long getClickedCount() {
        return clickedCount;
    }

    public void setClickedCount(Long clickedCount) {
        this.clickedCount = clickedCount;
    }
}

class AdProvinceTopN {
    private String timestamp;
    private String adID;
    private String province;
    private String clickedCount;

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getAdID() {
        return adID;
    }

    public void setAdID(String adID) {
        this.adID = adID;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getClickedCount() {
        return clickedCount;
    }

    public void setClickedCount(String clickedCount) {
        this.clickedCount = clickedCount;
    }
}

class AdTrendStat{
    private String _data;
    private String _hour;
    private String _minute;
    private String adID;
    private Long clickedCount;

    public String get_data() {
        return _data;
    }

    public void set_data(String _data) {
        this._data = _data;
    }

    public String get_hour() {
        return _hour;
    }

    public void set_hour(String _hour) {
        this._hour = _hour;
    }

    public String get_minute() {
        return _minute;
    }

    public void set_minute(String _minute) {
        this._minute = _minute;
    }

    public String getAdID() {
        return adID;
    }

    public void setAdID(String adID) {
        this.adID = adID;
    }

    public Long getClickedCount() {
        return clickedCount;
    }

    public void setClickedCount(Long clickedCount) {
        this.clickedCount = clickedCount;
    }
}

class AdTrendCountHistory {


    private Long clickedCountHistory;

    public Long getClickedCountHistory() {
        return clickedCountHistory;
    }

    public void setClickedCountHistory(Long clickedCountHistory) {
        this.clickedCountHistory = clickedCountHistory;
    }

}

