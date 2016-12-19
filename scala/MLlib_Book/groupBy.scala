package scala.MLlib_Book

import org.apache.spark.{SparkContext, SparkConf}

/**
 * Created by Administrator on 2016/4/10.
 */
object groupBy {

  def main(args: Array[String]) {

    val conf = new SparkConf().setMaster("local").setAppName("Cartesian")
    val sc = new SparkContext(conf)
    val arr = sc.parallelize(Array(1, 2, 3, 4, 5))
    val result = arr.groupBy(myFilter(_),1)//设置第一个分组
  }
  def myFilter(num:Int): Unit = {
    num >= 3
  }

}
