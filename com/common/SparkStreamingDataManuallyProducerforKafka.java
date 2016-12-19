package com.common;

/**
 * Created by Administrator on 2016/4/8.
 */


import kafka.javaapi.producer.Producer;
import kafka.producer.KeyedMessage;
import kafka.producer.ProducerConfig;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;
import java.util.Random;

/**
 * 论坛数据自动生成代码，数据格式如下：
 * date:日期，格式为yyyy-MM-dd
 * timestamp：时间戳
 * userID：用户ID
 * pageID：页面ID
 * channel：板块ID
 * action：点击和注册
 */
public class SparkStreamingDataManuallyProducerforKafka extends Thread{


    //论坛板块
    static String[] channelNames = new String[] {
            "spark","scala","kafka","Flink","hadoop","Storm",
            "Hive","Impala","Hbase","ML"
    };
    static  String[] actionNames = new String[]{"View","Register"};

    private String topic;//发送给Kafak的数据的类别
    private Producer<Integer,String> producerForKaka;

    private static  String dataToday;
    private static Random random;

    public SparkStreamingDataManuallyProducerforKafka(String topic){
        dataToday = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        this.topic = topic;
        random = new Random();
        Properties conf = new Properties();
        conf.put("metadata.broker.list", "master:9092,slave1:9092,slave2:9092");
        conf.put("serializer.class","kafka.serializer.StringEncoder");
        producerForKaka = new Producer<Integer,String>(new ProducerConfig(conf));
    }


    @Override
    public void run() {
        int counter = 0;
        while(true) {
            counter++;
            String userLog = userLogs();
            System.out.println("product:" + userLog);
            producerForKaka.send(new KeyedMessage<Integer, String>(topic,userLog));

            if(500 == counter) {
                counter = 0;
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void main(String[] args) {

        new SparkStreamingDataManuallyProducerforKafka("UserLogs").start();
    }

    private static String userLogs() {
        StringBuffer userLogBuffer = new StringBuffer("");
            long timestamp = new Date().getTime();
            long userID = 0L;
            long pageID = 0L;

            //随机生成的用户ID
            userID = random.nextInt((int) 2000);
            //随机生成的页面ID
            pageID = random.nextInt((int) 2000);
            //随机生成Chan
            String channel = channelNames[random.nextInt(10)];
            //随机生成action行为
            String action = actionNames[random.nextInt(2)];
            userLogBuffer.append(dataToday)
                    .append("\t")
                    .append(timestamp)
                    .append("\t")
                    .append(userID)
                    .append("\t")
                    .append(pageID)
                    .append("\t")
                    .append(channel)
                    .append("\t")
                    .append(action);
        return userLogBuffer.toString();
    }

}
