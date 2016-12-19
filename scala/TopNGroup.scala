package scala

import org.apache.spark.{SparkContext, SparkConf}

/**
 * Created by Administrator on 2016/1/26.
 */
object TopNGroup {
  def main(args: Array[String]) {
    val conf  = new SparkConf().setAppName("TopNGroup").setMaster("local");
    val sc = new SparkContext(conf)
    val lines = sc.textFile("E:/workspaces/data/TopNGroup.txt");
    val groupAndSort = lines.map(_.split(" ")).map(x => (x(0),x(1))).groupByKey().sortByKey(false).map(x => (x._1,x._2.toList.sortWith(_>_)));
    groupAndSort.foreach(x => {
      println(x._1)
      x._2.foreach(println)
    })
  }

}
