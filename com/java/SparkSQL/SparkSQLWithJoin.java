package com.java.SparkSQL;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.api.java.function.PairFunction;
import org.apache.spark.sql.DataFrame;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.RowFactory;
import org.apache.spark.sql.SQLContext;
import org.apache.spark.sql.types.DataTypes;
import org.apache.spark.sql.types.StructField;
import org.apache.spark.sql.types.StructType;
import scala.Tuple2;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/4/2.
 */
public class SparkSQLWithJoin {
    public static void main(String [] args) {
        SparkConf conf = new SparkConf().setMaster("local").setAppName("SparkSQLWithJoin");
        JavaSparkContext sc = new JavaSparkContext(conf);
        SQLContext sqlContext = new SQLContext(sc);

        DataFrame peopleDF = sqlContext.read().json("E:\\softwares\\spark-1.6.0-bin-hadoop2.6\\examples\\src\\main\\resources/peoples.json");
        peopleDF.registerTempTable("peopleScores");
        DataFrame execellentScoresDF = sqlContext.sql("select name,score from peopleScores where score > 90");
        execellentScoresDF.show();
        List<String> execellentScoresNameList = execellentScoresDF.javaRDD().map(new Function<Row, String>() {
            @Override
            public String call(Row row) throws Exception {
                return row.getAs("name");
            }
        }).collect();

        List<String> peopleInformations = new ArrayList<String>();
        peopleInformations.add("{\"name\":\"Michael\",\"age\":20}");
        peopleInformations.add("{\"name\":\"Andy\",\"age\":17}");
        peopleInformations.add("{\"name\":\"Justin\",\"age\":19}");

        JavaRDD<String> peopleInformationsRDD = sc.parallelize(peopleInformations);
        DataFrame peopleInformationsDF = sqlContext.read().json(peopleInformationsRDD);
        peopleInformationsDF.registerTempTable("peopleInformations");

        StringBuilder sqlText = new StringBuilder();
        sqlText.append("select name,age from peopleInformations where name in (");
        for(int i = 0;i<execellentScoresNameList.size();i++){
            sqlText.append("'" + execellentScoresNameList.get(i)+"'"+",");
        }
        sqlText.deleteCharAt(sqlText.length() - 1);
        sqlText.append(")");

        DataFrame execellentNameAgeDF = sqlContext.sql(sqlText.toString());
        execellentNameAgeDF.show();

        JavaPairRDD<String,Tuple2<Integer,Integer>> resultRDD = execellentScoresDF.javaRDD().mapToPair(new PairFunction<Row, String, Integer>() {
            @Override
            public Tuple2<String, Integer> call(Row row) throws Exception {
                return new Tuple2<String, Integer>(row.getAs("name"), (int)row.getLong(1));
            }
        }).join(execellentNameAgeDF.javaRDD().mapToPair(new PairFunction<Row, String, Integer>() {
            @Override
            public Tuple2<String, Integer> call(Row row) throws Exception {
                return new Tuple2<String, Integer>(row.getAs("name"),Integer.valueOf(String.valueOf(row.getAs("age"))));
            }
        }));

        JavaRDD<Row> resultRowRDD = resultRDD.map(new Function<Tuple2<String,Tuple2<Integer,Integer>>, Row>() {
            @Override
            public Row call(Tuple2<String, Tuple2<Integer, Integer>> tuple) throws Exception {
                return RowFactory.create(tuple._1(),tuple._2()._1(),tuple._2()._2());
            }
        });

        /**
         * 动态构造DateFrame的元数据
         */
        List<StructField>  structField = new ArrayList<StructField>();
        structField.add(DataTypes.createStructField("name", DataTypes.StringType, true));
        structField.add(DataTypes.createStructField("age", DataTypes.IntegerType, true));
        structField.add(DataTypes.createStructField("score", DataTypes.IntegerType, true));
        //构建StructType，用于最后DataFrame元数据的描述
        StructType structType = DataTypes.createStructType(structField);
        /**
         * 基于已有的MetaData以及RDD<ROW>来构造DataFrame
         */
        DataFrame personsDF = sqlContext.createDataFrame(resultRowRDD, structType);
        personsDF.show();

    }

}

