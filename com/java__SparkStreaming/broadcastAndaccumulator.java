package com.java__SparkStreaming;

import org.apache.spark.Accumulator;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.api.java.function.Function2;
import org.apache.spark.api.java.function.PairFunction;
import org.apache.spark.broadcast.Broadcast;
import org.apache.spark.streaming.Durations;
import org.apache.spark.streaming.Time;
import org.apache.spark.streaming.api.java.JavaPairDStream;
import org.apache.spark.streaming.api.java.JavaReceiverInputDStream;
import org.apache.spark.streaming.api.java.JavaStreamingContext;
import scala.Tuple2;

import java.util.List;


/**
 * Created by Administrator on 2016/4/13.
 */
public class broadcastAndaccumulator {
    private  static volatile Broadcast<List<String>> broadcastList = null;
    private  static volatile Accumulator<Integer> accumulator = null;
    public static void main(String[] args) {
        SparkConf conf = new SparkConf().setMaster("local[2]").setAppName("wordCountOnline");
        JavaStreamingContext jssc = new JavaStreamingContext(conf, Durations.seconds(5));

        broadcastList = jssc.sparkContext().broadcast(java.util.Arrays.asList("Hadoop","Mahout","Hive"));
        accumulator = jssc.sparkContext().accumulator(0, "OnlineBlackCounter");
        System.out.println(accumulator.value());

        JavaReceiverInputDStream lines = jssc.socketTextStream("master", 9999);
        JavaPairDStream<String,Integer> pairs = lines.mapToPair(new PairFunction<String, String, Integer>() {
            @Override
            public Tuple2<String, Integer> call(String word) throws Exception {
                return new Tuple2<String, Integer>(word, 1);
            }
        });

       JavaPairDStream<String,Integer> wordCount = pairs.reduceByKey(new Function2<Integer, Integer, Integer>() {
           @Override
           public Integer call(Integer v1, Integer v2) throws Exception {
               return v1 + v2;
           }
       });

        wordCount.foreachRDD(new Function2<JavaPairRDD<String, Integer>, Time, Void>() {
            @Override
            public Void call(JavaPairRDD<String, Integer> rdd, Time time) throws Exception {
                rdd.filter(new Function<Tuple2<String, Integer>, Boolean>() {
                    @Override
                    public Boolean call(Tuple2<String, Integer> wordPair) throws Exception {
                        if(broadcastList.value().contains((wordPair._1()))){
                            accumulator.add(wordPair._2());
                            return false;
                        } else {
                            return true;
                        }
                    }
                }).collect();
                System.out.println("BlackList appeared:" + accumulator.value()+ "times");
                return null;
            }
        });

        jssc.start();
        jssc.awaitTermination();
        jssc.close();

    }
}
