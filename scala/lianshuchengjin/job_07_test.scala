package scala.lianshuchengjin

import org.apache.spark.{SparkContext, SparkConf}

import scala.util.Random

/**
 * Created by Administrator on 2016/7/25.
 */
object job_07_test {
  def main(args:Array[String]): Unit = {

    val conf = new SparkConf().setMaster("local").setAppName("job_07")
    val sc = new SparkContext(conf)
    sc.setLogLevel("OFF")


    val rows = 10
    val cols = 10

  }

}
