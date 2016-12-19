package com.java__SparkStreaming;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.function.FlatMapFunction;
import org.apache.spark.api.java.function.Function2;
import org.apache.spark.api.java.function.PairFunction;
import org.apache.spark.streaming.Durations;
import org.apache.spark.streaming.api.java.*;
import scala.Tuple2;

import java.io.File;
import java.util.Arrays;

/**
 * Created by Administrator on 2016/4/17.
 */
public class wordCountOnLine {

    public static void main(String[] args) {
        /**
         * 配置SparkConf，
         * 1，至少2条线程，因为Spark Streaming 应用程序在运行的时候，至少有一条用于在现场不断的循环接受数据，并且
         * 至少有一条线程用户处理接受的数据（否组的话无法有线程用于处理数据，随着时间的推移，内存和磁盘都会不堪重负）
         * 2，对于集群而言，每个Executor一般肯定不止一个线程，对于处理spark streaming的应用程序，每个executor一般分配
         * 多少coree比较合适？根据过去的经验，5个左右的core是最佳的，
         */
        //SparkConf conf = new SparkConf().setMaster("local[2]").setAppName("wordCountOnLine");
        /**
         * 第二步：
         * 1.創建SparkStreamingContext，这个是SparkStreaming应用程序所有功能的起始点和程序调度的核心
         * SparkStreamingContext的构建可以基于SparkConf参数，也可基于持久化的SparkStreamingContext的内容来
         * 恢复过来
         * 2.在一个spark streaming应用程序中可以创建若干个SparkStreamingContext对象，使用下一个SparkStreamingContext
         * 之前需要把前面正在运行的SparkStreamingContext对象关闭掉，由此，我们获得一个重大的启发，SparkStreaming 也只是
         * spark core上的一个应用程序，只不过Spark Streaming 框架想要运行的话需要spark工程师写业务逻辑代码。
         */
        //JavaStreamingContext jssc = new JavaStreamingContext(conf, Durations.seconds(5));
        final String checkpointDirectory = "hdfs://master:9000/sparkStreaming/checkPoint_Data";
        JavaStreamingContextFactory factory = new JavaStreamingContextFactory() {
            @Override
            public JavaStreamingContext create() {
                return createContext(checkpointDirectory);
            }
        };
        /**
         * 可以从失败中回复Driver，不过还需要指定Driver这个进程运行在Cluster，并且在提交应用程序
         * 的时候指定--supervise；
         */
        JavaStreamingContext jssc = JavaStreamingContext.getOrCreate(checkpointDirectory, factory);

        /**
         * 第三步：创建SparkStreaming输入数据来源input Streaming
         *  如果经常在时间段内没有数据的话，不断的启动空任务会造成调度资源的浪费。
         */
        JavaReceiverInputDStream lines = jssc.socketTextStream("master", 9999);
        /**
         * 接下来就像对于RDD编程一样基于DStream进行编程！！！原因是DStream是RDD产生的模板（或者说类）
         * 在spark Streaming发生计算前，其实质是吧每个DStream的操作翻译成为对RDD的操作。
         */
        JavaDStream<String> words = lines.flatMap(new FlatMapFunction<String,String>() {
            @Override
            public Iterable<String> call(String line) throws Exception {
                return Arrays.asList(line.split(" "));
            }

        });
        JavaPairDStream<String,Integer> pairs = words.mapToPair(new PairFunction<String, String, Integer>() {
            @Override
            public Tuple2<String, Integer> call(String word) throws Exception {
                return new Tuple2<String, Integer>(word, 1);
            }
        });
        JavaPairDStream<String,Integer> wordsCount = pairs.reduceByKey(new Function2<Integer, Integer, Integer>() {
            @Override
            public Integer call(Integer v1, Integer v2) throws Exception {
                return v1+v2;
            }
        });
        /**
         * 此处的print并不会直接触发job的执行，因为现在的一切都是在sparkSteaming框架的控制之下的，对于spark streaming，
         * 具体是否触发真正的娥job是基于duration时间间隔触发的。
         * Spark Streaming应用陈旭要想执行具体的Job，对DStream就必须有output Stream操作
         * output Strram有很多类型的函数触发，例如print，saveASTextFile，齐总最为重要的一个方法是
         * foreachRDD，以为Spark Streaming处理的结果一般都会放在Redis。DB，等上面，foreachRDD
         * 主要就是用来完成这些功能的，而且可以随意的指定具体数据到底放在那里。
         */
        wordsCount.print();
        /**
         * spark Streaming执行引擎也就是Dirver开始运行，Dirver启动的时候是位于一条
         * 新的线程中的，其内部有消息循环体，用于接收应用程序本省或者executor中的消息
         */
        jssc.start();
        jssc.awaitTermination();
        jssc.close();

    }

    private static JavaStreamingContext createContext(String checkpointDirectory) {

        // If you do not see this printed, that means the StreamingContext has been loaded
        // from the new checkpoint
        System.out.println("Creating new context");
        SparkConf sparkConf = new SparkConf().setAppName("wordCountOnLine");
        // Create the context with a 1 second batch size
        JavaStreamingContext ssc = new JavaStreamingContext(sparkConf, Durations.seconds(30));
        ssc.checkpoint(checkpointDirectory);
        return ssc;
    }
}
