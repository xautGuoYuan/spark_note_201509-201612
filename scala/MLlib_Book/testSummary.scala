package scala.MLlib_Book

import org.apache.spark.mllib.linalg.Vectors
import org.apache.spark.mllib.stat.Statistics
import org.apache.spark.{SparkContext, SparkConf}

/**
 * Created by Administrator on 2016/4/11.
 */
object testSummary {

  def main(args: Array[String]) {
    val conf = new SparkConf().setMaster("local").setAppName("Cartesian")
    val sc = new SparkContext(conf)
    val rdd = sc.parallelize(Array("1","2","3","4","5"))
      .map(_.split(" ").map(_.toDouble))
      .map(line => Vectors.dense(line))
    val summary = Statistics.colStats(rdd)
    println(summary.mean)
    println(summary.variance)
    println(summary.normL1)//计算曼哈段距离 x1+x2+x3+...
    println(summary.normL2)//计算欧几里德距离
  }

}
