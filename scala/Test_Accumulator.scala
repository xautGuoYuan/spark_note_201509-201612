package scala

import org.apache.spark.{SparkContext, SparkConf}

/**
 * Created by Administrator on 2016/1/30.
 */
object Test_Accumulator {

  def main(args: Array[String]) {
    val conf = new SparkConf().setAppName("Accumulator").setMaster("local")
    val sc = new SparkContext(conf)

    val sum = sc.accumulator(0)
    val data = sc.parallelize(1 to 100)
    data.foreach(x => sum.add(x))
    println(sum)
  }
}
