package com.java__SparkStreaming;

import kafka.javaapi.producer.Producer;
import kafka.producer.KeyedMessage;
import kafka.producer.ProducerConfig;

import java.util.*;

/**
 * Created by Administrator on 2016/7/12.
 */
public class MockAdClickedStats {

    public static void main(String[] args ){

        Random random = new Random();
        String[] provinces = new String[]{"Guanddong,","Zhejiang","Jiangsu","Fujian"};
        Map<String,String[]> cities = new HashMap<String,String[]>();
        cities.put("Guanddong",new String[]{"Guangzhou","Shenzhen","DongGuan"});
        cities.put("Zhejiang",new String[]{"Hangzhou","Wenzhou","Ningbo"});
        cities.put("Jiangsu",new String[]{"Nanjing","Suzhou","Wuxi"});
        cities.put("Fujian",new String[]{"Fuzhou","Xiamen","Sanming"});
        String[] ips = new String[]{
                "192.168.111.0",
                "192.168.111.1",
                "192.168.111.2",
                "192.168.111.3",
                "192.168.111.4"
        };

        /**
         * kafka相关的基本配置信息
         */
        Properties kafkaConf = new Properties();
        kafkaConf.put("serializer.class","kafka.serializer.StringEncoder");
        kafkaConf.put("metadata.broker.list","master:9092,slave1:9092,slavw2:9092");
        ProducerConfig producerConfig = new ProducerConfig(kafkaConf);

        Producer<Integer,String> producer = new Producer<Integer,String>(producerConfig);

        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    //数据格式：tiemstamp,ip,userID,adID,province,city
                    Long timestamp = new Date().getTime();
                    String ip = ips[random.nextInt(5)];//可以采用网络上免费提供的IP库
                    int userID = random.nextInt(10000);
                    int adID = random.nextInt(100);
                    String province = provinces[random.nextInt(4)];
                    String city = cities.get(province)[random.nextInt(3)];
                    String clickedAd = timestamp + "\t" + ip + "\t" + userID + "\t" +
                            adID + "\t" + province + "\t" + city;
                    producer.send(new KeyedMessage("AdClicked", "clickedAd"));//topic和消息本身
                    try {
                        Thread.sleep(50);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }
}
