package com.Java_Spark.www;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.api.java.function.PairFunction;
import org.apache.spark.api.java.function.VoidFunction;
import scala.Tuple2;

/**
 * ¶þ´ÎÅÅÐò
 * Created by Administrator on 2016/1/25.
 */
public class SecondarySortApp {
    public  static void main(String [] args) {
        SparkConf conf = new SparkConf().setAppName("SecondarySortApp").setMaster("local");
        JavaSparkContext sc = new JavaSparkContext(conf);
        JavaRDD<String> lines = sc.textFile("E:/workspaces/data/hello.txt");
        JavaPairRDD<SecondarySortKey,String> pair = lines.mapToPair(new PairFunction<String, SecondarySortKey, String>() {
            @Override
            public Tuple2<SecondarySortKey, String> call(String line) throws Exception {
                String[] splited = line.split(" ");
                SecondarySortKey k = new SecondarySortKey(Integer.valueOf(splited[0]), Integer.valueOf(splited[1]));
                return new Tuple2<SecondarySortKey, String>(k, line);
            }
        });

        JavaPairRDD<SecondarySortKey,String> sorted = pair.sortByKey();
        JavaRDD<String> sortedResult = sorted.map(new Function<Tuple2<SecondarySortKey, String>, String>() {
            @Override
            public String call(Tuple2<SecondarySortKey, String> secondarySortKeyStringTuple2) throws Exception {
                return secondarySortKeyStringTuple2._2();
            }
        });
        sortedResult.foreach(new VoidFunction<String>() {

            @Override
            public void call(String s) throws Exception {
                System.out.println(s);
            }
        });

        sc.stop();

    }
}
