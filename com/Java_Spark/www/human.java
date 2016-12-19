package com.Java_Spark.www;

/**
 * Created by Administrator on 2016/3/9.
 */
public abstract class human {
    private int age;
    private int tizhong;
    public int getAge() {
        return age;
    }
    public void setAge(int age) {
        this.age = age;
    }
    public int getTizhong() {
        return tizhong;
    }
    public void setTizhong(int tizhong) {
        this.tizhong = tizhong;
    }
    public human(int age, int tizhong){
        this.age = age;
        this.tizhong = tizhong;
    }
    public human(){
    }
    public abstract void max();
}