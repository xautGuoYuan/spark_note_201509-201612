package com.Java_Spark.www;

/**
 * Created by Administrator on 2016/3/21.
 */
public class Thread_Test {
    private static boolean flag = false;
    public static void main(String[] args) throws Exception{

        Thread a = new Thread() {
            public void run() {
                for(int i = 0;i <= 10;i++) {
                    try {
                        System.out.println("a:" + i);
                        sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                }
        };


        Thread b = new Thread() {
            public void run() {
                for(int i = 10;i >= 0;i--) {
                    try {
                        System.out.println("b:" + i);
                        sleep(1100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        };

        a.start();
        b.start();
    }
}

