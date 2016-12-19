package com.java.SparkSQL;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.VoidFunction;
import org.apache.spark.sql.DataFrame;
import org.apache.spark.sql.DataFrameReader;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SQLContext;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.Iterator;

/**
 * Created by Administrator on 2016/4/3.
 */
public class SparkSQLJDBC2MySQL {

    public static void main(String [] args) {
        SparkConf conf = new SparkConf().setMaster("local").setAppName("SparkSQLJDBC2MySQL");
        JavaSparkContext sc = new JavaSparkContext(conf);
        SQLContext sqlContext = new SQLContext(sc);
        /**
         * 1,通过format("jdbc")的方式说明sparkSQL操作的数据来源是通过JDBC获得的。JDBC后端一般都是数据库，例如
         * MySQL、Oracle等
         * 2,通过DataFrameReader的option方法吧要访问的数据库的信息传递进去；
         *      url：代表数据库的jdbc链接地址；
         *      dbtable：具体要链接使用哪个数据库
         * 3，Driver部分是Spark SQL访问数据库的具体的驱动的完整包名和类名；
         * 4，关于JDBC的驱动的Jar，可以放在Spark的library目录，也可以在使用SparkSubmit的时候指定具体的jar（
         *       编译和打包的时候都不需要这个JDBC的Jar）
         */
        DataFrameReader reader = sqlContext.read().format("jdbc");
        reader.option("url","jdbc:mysql://master:3306/spark");
        reader.option("btable","modmo");
        reader.option("driver","com.mysql.jdbc.Driver");
        reader.option("user","root");
        reader.option("password", "root");
        /**
         * 在实际的企业级开发环境中，如果数据中数据规模特别大，例如10亿条数据，此时采用传统的DB去处理的话，
         * 一般需要对10亿条数据分成许多批次处理，例如分成 100批（受限于单台Server的处理能力）,且实际的处理过程
         * 可能会非常复杂，通过传统的Java EE的跟你熟可以很难或者不方便实现处理算法，此时采用Spark SQL获得数据库中的
         * 数据并进行分布式处理就可以非常好的解决该问题，但是由于Spark SQL加载DB中的数据需要时间，所以一般会在
         * Spark SQL和具体要操作的DB之间加上一个缓冲层次（redis,tachyon）;例如中间使用Redis，可以吧Spark吃力速度提高到
         * 甚至45倍；
         */
        DataFrame momoDataSourceDFFrameFromMySQL = reader.load();//基于momo表创建DataFram
        momoDataSourceDFFrameFromMySQL.show();
        momoDataSourceDFFrameFromMySQL.registerTempTable("test1");
        sqlContext.sql("select name from test1").show();


        reader.option("dbtable", "qianqian");
        DataFrame qianqianDataSourceDFFrameFromMySQL = reader.load();//基于qianqian表创建DataFram
        qianqianDataSourceDFFrameFromMySQL.show();
        qianqianDataSourceDFFrameFromMySQL.registerTempTable("test2");
        sqlContext.sql("select age from test2").show();


        sqlContext.sql("select name,age from test1 a  JOIN test2 b ON a.id = b.id").show();

        DataFrame personDF = sqlContext.sql("select name,age from test1 a  JOIN test2 b ON a.id = b.id");
        /**
         * 1,当DataFrame要把通过SparkSQL、cora、ML等复杂操作后的数据写入数据库的时候首先是权限的问题，确保数据库
         * 授权了当前操作SparkSQL的用户；
         * 2，DataFrame要写数据到DB的时候一般都不可以直接写进去，而是要转成RDD，通过RDD写数据到DB中。
         */
        System.out.println("*************************");
        personDF.javaRDD().foreachPartition(new VoidFunction<Iterator<Row>>() {
            @Override
            public void call(Iterator<Row> rowIterator) throws Exception {
                Connection conn2MySQL = DriverManager.getConnection("jdbc:mysql://master:3306/spark", "root", "root");
                Statement statement = conn2MySQL.createStatement();
                while (rowIterator.hasNext()) {
                    StringBuilder sqlText = new StringBuilder();
                    sqlText.append("Insert into hebing(name,age) values( ");
                    Row row = rowIterator.next();
                    sqlText.append("'" + row.getAs("name").toString() + "'" + ",");
                    sqlText.append(row.getAs("age").toString()+")");
                    statement.execute(sqlText.toString());
                }
            }
        });































    }

}
