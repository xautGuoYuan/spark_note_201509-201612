package com;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.Function;

import java.util.Arrays;
import java.util.List;

/**
 * Created by Administrator on 2016/7/15.
 */
public class test {

    public static void main(String[] args) {
        SparkConf conf = new SparkConf().setMaster("local").setAppName("RDD2DataFrameByProgrammatically");
        JavaSparkContext sc = new JavaSparkContext(conf);
        Person person = new Person();
        List<Integer> data = Arrays.asList(1, 2, 3, 4);
        JavaRDD<Integer> distData = sc.parallelize(data,2);
        distData.map(new Function<Integer, Integer>() {
            @Override
            public Integer call(Integer item) throws Exception {
                person.add(item);
                return null;
            }
        });


    }
}
class Person implements java.io.Serializable {
    public void add(Integer a) {
    }
}
