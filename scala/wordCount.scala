package scala

import org.apache.spark.{SparkContext, SparkConf}

/**
 * Created by Administrator on 2016/1/25.
 */
object wordCount {
  def main(args: Array[String]) {
    val conf = new SparkConf().setAppName("WordCount")
    val sc = new SparkContext(conf)

    sc.textFile("E:\\softwares\\spark-1.6.0-bin-hadoop2.6/README.md").flatMap(_.split(" ")).map(word => (word,1)).reduceByKey(_+_).map(x=>(x._2,x._1)).sortByKey(false).map(x=>(x._2,x._1)).
    saveAsTextFile("E:\\output/cc.txt")
  }
}
