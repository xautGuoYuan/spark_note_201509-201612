package scala

import org.apache.spark.{SparkContext, SparkConf}

/**
 * Created by Administrator on 2016/1/30.
 */
object Broadcast {

  def main(args: Array[String]) {
    val conf = new SparkConf().setAppName("Broadcast").setMaster("local")
    val sc = new SparkContext(conf)

    val  broadcastNumber = sc.broadcast(10);
    val data = sc.parallelize(1 to 100)
    data.map(x => x*broadcastNumber.value).foreach(println)
  }
}
