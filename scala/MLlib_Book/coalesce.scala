package scala.MLlib_Book

import org.apache.spark.{SparkContext, SparkConf}

/**
 * Created by Administrator on 2016/4/10.
 */
/**
 * 重新分区
 */
object coalesce {

  def main(args: Array[String]) {

    val conf = new SparkConf().setMaster("local").setAppName("coalesce")
    val sc = new SparkContext(conf)
    val arr = sc.parallelize(Array(1,2,3,4,5,6))
    println(arr.partitions.length)//打印分区数
    val arr2 = arr.coalesce(2,true)
    println(arr2.partitions.length)//打印分区数
    val result = arr.aggregate(10)(math.max(_,_),_+_)
    println(result)
    val result2 = arr2.aggregate(10)(math.max(_,_),_+_)
    println(result2)
  }

}
