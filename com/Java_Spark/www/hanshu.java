package com.Java_Spark.www;

/**
 * Created by Administrator on 2016/3/9.
 */
public class hanshu extends human {
    public hanshu(){

    }
    public hanshu(int age, int tizhong){
        super(age, tizhong);
    }
    public void max(){
        System.out.println("ÄêÁäÌåÖØ"+(this.getAge()+this.getTizhong()));
    }
}