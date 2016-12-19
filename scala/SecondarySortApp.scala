package scala

import org.apache.spark.{SparkContext, SparkConf}

/**
 * ¶þ´ÎÅÅÐò
 * Created by Administrator on 2016/1/25.
 */
object SecondarySortApp {
  def main(args: Array[String]) {
    val conf = new SparkConf().setAppName("SecondarySortApp").setMaster("local")
    val sc = new SparkContext(conf)
    val lines = sc.textFile("E:/workspaces/data/hello.txt")
    val pairWithSortKey = lines.map(line => {
      (SecondarySortKey(line.split(" ")(0).toInt,line.split(" ")(1).toInt),line)
    })
    val sorted = pairWithSortKey.sortByKey()
    val sortedResult = sorted.map(_._2)
    sortedResult.collect.foreach(println)
    sc.stop()
  }
}
