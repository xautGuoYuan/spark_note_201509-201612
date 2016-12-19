package scala.MLlib_Book

import org.apache.spark.mllib.stat.Statistics
import org.apache.spark.{SparkContext, SparkConf}

/**
 * Created by Administrator on 2016/4/11.
 */
object testCorrect_pierxun {

  def main(args: Array[String]) {
    val conf = new SparkConf().setMaster("local").setAppName("Cartesian")
    val sc = new SparkContext(conf)
    val rddPoint1 = sc.parallelize(Array("1 2 3 4 5"))
      .flatMap(_.split(" ").map(_.toDouble))
    val rddPoint2 = sc.parallelize(Array("2 4 6 8 10"))
      .flatMap(_.split(" ").map(_.toDouble))
    val correlation:Double = Statistics.corr(rddPoint1,rddPoint2)
    println(correlation)
  }

}
