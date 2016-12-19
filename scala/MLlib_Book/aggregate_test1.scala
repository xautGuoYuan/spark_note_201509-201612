package scala.MLlib_Book

import org.apache.spark.{SparkConf, SparkContext}

/**
 * Created by Administrator on 2016/4/10.
 */
object aggregate_test1 {

  def main (args: Array[String]){
    val conf  = new SparkConf().setMaster("local").setAppName("aggregate_test1")
    val sc = new SparkContext(conf);
    val arr = sc.parallelize(Array(1,2,3,4),1)
    val result = arr.aggregate(5)(math.max(_,_),_+_)
    println(result)
  }

}
