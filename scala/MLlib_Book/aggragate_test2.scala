package scala.MLlib_Book

import org.apache.spark.{SparkContext, SparkConf}

/**
 * Created by Administrator on 2016/4/10.
 */
object aggragate_test2 {

  def main(args: Array[String]) {

    val conf = new SparkConf().setMaster("local").setAppName("aggragate_test2")
    val sc = new SparkContext(conf)
    val arr = sc.parallelize(Array(1, 2, 3, 4, 5, 6, 7, 8), 2)
    val result = arr.aggregate(10)(math.max(_, _), _ + _)
    println(result)
  }

}
