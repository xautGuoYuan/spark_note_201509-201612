package scala

import org.apache.spark.{SparkContext, SparkConf}

/**
 * Created by Administrator on 2016/4/13.
 */
import org.apache.spark.{SparkContext, SparkConf}
object test_break {
  def main(args: Array[String]){
    val conf = new SparkConf().setAppName("hello").setMaster("local")
    val sc = new SparkContext(conf)
    var n:Int = 10
    for(c <- "hello word") {
      if (5 == n)
        scala.util.control.Breaks.break
      else {
        n -= 1
        println(c)
      }
    }

  }
}