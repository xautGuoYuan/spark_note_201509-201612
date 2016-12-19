package com.Java_Spark.www;

import groovy.ui.SystemOutputInterceptor;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.PairFunction;
import org.apache.spark.api.java.function.VoidFunction;
import scala.Tuple2;
import scala.actors.threadpool.Arrays;

import java.util.Iterator;

/**
 * Created by Administrator on 2016/1/25.
 */
public class TopNGroup {

    public static void main(String [] args) {

        SparkConf conf = new SparkConf().setAppName("TopNGroup").setMaster("local");
        JavaSparkContext sc = new JavaSparkContext(conf);
        JavaRDD<String> lines = sc.textFile("E:/workspaces/data/TopNGroup.txt");
        JavaPairRDD<String,Integer> pair = lines.mapToPair(new PairFunction<String, String, Integer>() {
            @Override
            public Tuple2<String, Integer> call(String line) throws Exception {
                String[] splited = line.split(" ");
                return new Tuple2<String, Integer>(splited[0], Integer.valueOf(splited[1]));
            }
        });
        JavaPairRDD<String, Iterable<Integer>> groupedPaired = pair.groupByKey();
        JavaPairRDD<String, Iterable<Integer>> groupedPairedAndSort = groupedPaired.sortByKey();

        JavaPairRDD<String,Iterable<Integer>> top5 = groupedPairedAndSort.mapToPair(new PairFunction<Tuple2<String,Iterable<Integer>>, String, Iterable<Integer>>() {
            @Override
            public Tuple2<String, Iterable<Integer>> call(Tuple2<String, Iterable<Integer>> groupedData) throws Exception {
                Integer[] top5 = new Integer[5];
                String groupedKey = groupedData._1();
                Iterator<Integer> groupedValue = groupedData._2().iterator();
                while (groupedValue.hasNext()) {
                    Integer value = groupedValue.next();

                    for(int i = 0; i < 5; i++) {
                        if(top5[i] == null) {
                            top5[i] = value;
                            break;
                        } else if(value>top5[i]) {
                            for(int j = 4;j > i;j--) {
                                top5[j] = top5[j-1];
                            }
                            top5[i] = value;
                            break;
                        }
                    }
                }
                return new Tuple2<String, Iterable<Integer>>(groupedKey, Arrays.asList(top5));
            }
        });

        top5.foreach(new VoidFunction<Tuple2<String, Iterable<Integer>>>() {
            @Override
            public void call(Tuple2<String, Iterable<Integer>> toped) throws Exception {
                System.out.println("Group Key:" + toped._1());
                Iterator topedValue = toped._2().iterator();
                while (topedValue.hasNext()) {
                    System.out.println(topedValue.next());
                }
                System.out.println("*******************");
            }
        });

        sc.stop();




    }
}
