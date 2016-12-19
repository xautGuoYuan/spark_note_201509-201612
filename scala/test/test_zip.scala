package scala.test

import org.apache.spark.{SparkContext, SparkConf}

/**
 * Created by Administrator on 2016/9/10.
 */
object test_zip {

  def main(args: Array[String]) {

    val conf = new SparkConf().setMaster("local").setAppName("zip")
    val sc  = new SparkContext(conf)

    //RDD
    val a = sc.parallelize(List(1,2,3,4,5,6,7))
    val b = sc.parallelize(List("a","b","c","d","e","f","g"))
    a.zip(b).foreach(println)
    a.zipWithIndex().zip(b).foreach(println)


    //Collection
    val c = List(1,2,3,4,5,6,7,8,9)
    val d = List("a","b","c","d","e","f","g")
    c.zip(d).foreach(println)



  }

}
