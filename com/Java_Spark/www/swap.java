package com.Java_Spark.www;

import java.util.Scanner;

/**
 * Created by Administrator on 2016/3/7.
 */
public class swap {
    public static void main(String [] args){
        System.out.println("请输入两个整数！");
        Scanner scan = new Scanner(System.in);
        int one =  scan.nextInt();
        int two = scan.nextInt();
        Data data = new Data();
        data.x = one;
        data.y = two;
         Data changed = change(data);
        pr(changed);
    }

    static Data change(Data data){
        int one = (int)data.x;
        int two = (int)data.y;
        int flag;
        flag = one;
        one = two;
        two = flag;
        Data dd = new Data();
        dd.x = one;
        dd.y = two;
        return dd;
    }
    static void pr(Data data) {
        System.out.println(data.x);
        System.out.println(data.y);
    }
}
