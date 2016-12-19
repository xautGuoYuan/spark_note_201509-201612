package scala.spark_source_test

import org.apache.spark.{SparkConf, SparkContext}

/**
 * Created by Administrator on 2016/7/26.
 */
object job_02 {

  def main(args:Array[String]): Unit = {

    val conf = new SparkConf().setMaster("local").setAppName("job_02")
    val sc = new SparkContext(conf)

    val data = sc.textFile("D:\\BaiduYunDownload\\spark源码导读\\02从WordCount引发的代码入口\\users_txt").map(_.split(",")).cache()
    val test1 = data.map(x => (x(3),1)).reduceByKey(_+_,1)
    test1.foreach(println)

    val test2 = data.map(x => (x(2).substring(0,3),1)).reduceByKey(_+_,1)
    test2.foreach(println)
  }

}
