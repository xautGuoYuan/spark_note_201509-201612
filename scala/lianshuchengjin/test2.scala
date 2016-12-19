package scala.lianshuchengjin

import breeze.linalg._
import org.apache.spark.{SparkContext, SparkConf}

/**
 * Created by Administrator on 2016/7/5.
 */
object test2 {

  def main(args: Array[String]) {

    val conf = new SparkConf().setAppName("job_08").setMaster("local")
    val sc = new SparkContext(conf)

    val input = Array(
      "a,b,c,b,b,c,c,c",
      "x,y,z,z,z,z"
    )
    val data = sc.parallelize(input)
    data.zipWithIndex().map{ line =>
      val m = line._1.split(",")
        .map(word => line._2+"_"+word)
      (m,1)
    }.reduceByKey(_+_).map{ line =>
    }

  }
}
