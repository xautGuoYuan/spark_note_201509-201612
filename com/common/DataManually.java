package com.common;

/**
 * Created by Administrator on 2016/4/8.
 */


import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
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
public class DataManually {


    //论坛板块
    static String[] channelNames = new String[] {
            "spark","scala","kafka","Flink","hadoop","Storm",
            "Hive","Impala","Hbase","ML"
    };
    static  String[] actionNames = new String[]{"View","Register"};
    static String yesterdayFormated;

    public static void main(String[] args) {
        /**
         * 生成的数据条数
         */

        long numberItems = 5000;
        String path = ".";
        if(args.length > 0) {
            numberItems = Integer.valueOf(args[0]);
            path = args[1];
        }
        System.out.println("user log number is :" + numberItems);

        /**
         * 昨天的时间的生成
         */
        yesterdayFormated = yesterday();
        
        userLogs(numberItems,path);
        
    }

    private static void userLogs(long numberItems, String path) {
        StringBuffer userLogBuffer = new StringBuffer();
        Random random = new Random();
        for(int i = 0; i<numberItems;i++) {
            long timestamp = new Date().getTime();
            long userID = 0L;
            long pageID = 0L;

            //随机生成的用户ID
            userID = random.nextInt((int) numberItems);
            //随机生成的页面ID
            pageID = random.nextInt((int) numberItems);
            //随机生成Chan
            String channel = channelNames[random.nextInt(10)];
            //随机生成action行为
            String action = actionNames[random.nextInt(2)];
            userLogBuffer.append(yesterdayFormated)
                    .append("\t")
                    .append(timestamp)
                    .append("\t")
                    .append(userID)
                    .append("\t")
                    .append(pageID)
                    .append("\t")
                    .append(channel)
                    .append("\t")
                    .append(action)
                    .append("\n");
        }
        //System.out.print(userLogBuffer.toString());
        PrintWriter printWriter = null;
        try {
            printWriter = new PrintWriter(new OutputStreamWriter(new FileOutputStream(path+"userLog.log")) );
            printWriter.write(userLogBuffer.toString());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (printWriter != null) {
                printWriter.close();
            }
        }

    }

    public static String yesterday() {
        SimpleDateFormat data = new SimpleDateFormat("yyyy-mm-dd");
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE,-1);

        Date yesterday = cal.getTime();
        return data.format(yesterday);
    }


}
