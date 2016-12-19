package scala

import org.apache.spark.{SparkContext, SparkConf}

/**
 * Created by Administrator on 2016/1/30.
 */
object Test_Jion {

  def main(args: Array[String]) {

    val conf = new SparkConf().setAppName("Test_Join").setMaster("local")
    val sc = new SparkContext(conf)
    val names = sc.parallelize(Array((1,"Spark"),(2,"Scala"),(3,"Hadoop")))
    val scores = sc.parallelize(Array((1,100),(2,90),(3,80)))
    val namesAndScores = names.join(scores)
    namesAndScores.foreach(println(_))
  }

}
