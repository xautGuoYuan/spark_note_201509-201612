package com.java.SparkSQL;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.sql.DataFrame;
import org.apache.spark.sql.SQLContext;

/**
 * Created by Administrator on 2016/3/15.
 */
public class DataFrameOps {

    public static void main(String [] args) {

        SparkConf conf = new SparkConf().setAppName("DataFrameOps").setMaster("local");
        JavaSparkContext sc = new JavaSparkContext(conf);
        //创建SQLContext上下文对象用于SQL的分析
        SQLContext sqlContext = new SQLContext(sc);
        //创建DataFrame，可以简单的认为DataFrame是一张表
        DataFrame df= sqlContext.read().json("E:/softwares/spark-1.6.0/examples/src/main/resources/people.json");

        //select * from table
        df.show();

        //desc table;
        df.printSchema();

        //select name from table;
        df.select("name").show();

        //select name age from table;
        df.select("name","age").show();
        df.select(df.col("name"),df.col("age")).show();

        //select name,age+10 from table;
        df.select(df.col("name"),df.col("age").plus(10)).show();

        //select * from table where age > 10
        df.filter(df.col("age").gt(10)).show();

        //select count(*) from table group by age;
        df.groupBy(df.col("age")).count().show();
    }
}
