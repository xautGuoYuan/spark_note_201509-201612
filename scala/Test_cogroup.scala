package scala

import org.apache.spark.{SparkContext, SparkConf}

/**
 * Created by Administrator on 2016/1/30.
 */
object Test_cogroup {

  def main(args: Array[String]) {

    val conf = new SparkConf().setAppName("Test_cogroup").setMaster("local")
    val sc = new SparkContext(conf)

    val names = sc.parallelize(Array((1,"Spark"),(2,"Scala"),(3,"Hadoop")))
    val scores = sc.parallelize(Array((1,100),(2,90),(3,80),(1,234)))
    val namesAndScores = names.cogroup(scores)
    namesAndScores.foreach(x => {
      println("ID:" + x._1)
      println("Name" + x._2._1)
      println("Score:" + x._2._2)
    })

  }
}
