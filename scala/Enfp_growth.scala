package scala

/**
 * Created by Administrator on 2016/4/21.
 */

import org.apache.spark.mllib.fpm.FPGrowth
import org.apache.spark.rdd.RDD
import org.apache.spark.{SparkConf, SparkContext}


object Enfp_growth {
  def main(args: Array[String]) {
    val conf = new SparkConf().setAppName("EnFP_Growth")
    val sc = new SparkContext(conf)
    val transactions = sc.textFile("/fp/webdocs.dat").map(_.split(" ")).cache()
    val model = new FPGrowth()
      .setMinSupport(0.1)
      .setNumPartitions(20)
      .run(transactions)

    println(s"Number of frequent itemsets: ${model.freqItemsets.count()}")

    model.freqItemsets.collect().foreach { itemset =>
      println(itemset.items.mkString("[", ",", "]") + ", " + itemset.freq)
    }
    sc.stop()
  }
}