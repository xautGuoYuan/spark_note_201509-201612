package scala.MLlib_Book

import org.apache.spark.{SparkContext, SparkConf}

/**
 * Created by Administrator on 2016/4/11.
 */
/**
 * sortBy方法主要有3个参数，第一个为传入方法，用于计算排序的数据
 * 第二个是指定排序的值按升序还是降序显示。
 * 第三个是分片的数量。
 */
object sortBy {

  def main(args: Array[String]) {

    val conf = new SparkConf().setMaster("local").setAppName("Cartesian")
    val sc = new SparkContext(conf)
    val arr = sc.parallelize(Array((5,"b"),(6,"a"),(1,"f"),(3,"d"),(4,"c"),(2,"e")))
    val str1 = arr.sortBy(word => word._1,true) //按照第一 个数据排序
    val str2 = arr.sortBy(word => word._2,true) //按照第一个数据排序
    str1.foreach(println)
    str2.foreach(println)
  }
}
