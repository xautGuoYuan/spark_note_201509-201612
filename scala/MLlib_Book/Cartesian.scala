package scala.MLlib_Book

import org.apache.spark.{SparkContext, SparkConf}

/**
 * Created by Administrator on 2016/4/10.
 */
/**
 * µÑ¿¨¶û»ý
 */
object Cartesian {

  def main(args: Array[String]) {

    val conf = new SparkConf().setMaster("local").setAppName("Cartesian")
    val sc = new SparkContext(conf)
    val arr = sc.parallelize(Array(1,2,3,4,5,6))
    val arr2 = sc.parallelize(Array(6,5,4,3,2,1))
    val result = arr.cartesian(arr2)
    result.foreach(println)
  }
}
