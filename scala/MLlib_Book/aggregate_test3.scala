package scala.MLlib_Book

import org.apache.spark.{SparkContext, SparkConf}

/**
 * Created by Administrator on 2016/4/10.
 */
object aggregate_test3 {

  def main(args: Array[String]) {

    val conf = new SparkConf().setMaster("local").setAppName("aggragate_test3")
    val sc = new SparkContext(conf)
    val arr = sc.parallelize(Array("abc","b","c","d","e","f"),3)
    val result = arr.aggregate("@----")((value,word) => value + word, _ + _)
    println(result)
  }
}
