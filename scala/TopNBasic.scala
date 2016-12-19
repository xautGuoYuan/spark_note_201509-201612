package scala

import org.apache.spark.{SparkContext, SparkConf}

/**
 * Created by Administrator on 2016/1/25.
 */
object TopNBasic {
  def main(args: Array[String]) {
    val conf = new SparkConf().setAppName("TopNBasic").setMaster("local")
    val sc = new SparkContext(conf)
  }
}
