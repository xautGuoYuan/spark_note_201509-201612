package scala

import org.apache.spark.{SparkContext, SparkConf}

/**
 * Created by Administrator on 2016/8/17.
 */
object xxxxxxxxxxxx {

  def main(args: Array[String]) {
    val conf = new SparkConf().setAppName("WordCount").setMaster("local")
    val sc = new SparkContext(conf)

    val list = Array(
      "2016.8.1.1,a,11.11",
      "2016.8.1.2,a,11.22",
      "2016.8.1.4,a,22.23",
      "2016.8.1.3,a,23.25",
      "2016.8.1.5,a,11.26",
      "2016.8.1.6,a,88.28",
      "2016.8.1.7,a,11.30",
      "2016.8.1.8,a,77.33",
      "2016.8.2.9,a,11.35",
      "2016.8.2.10,a,11.39")

    val data = sc.parallelize(list,8).map(line => line.split(",")).map(line => (line(0).substring(0,8),line(0).substring(9,10),line(1),line(2)))
      .map(line => (line._1,line._2+"_" + line._3+"_" + line._4)).groupByKey().map(line => (line._1,line._2.toArray.sortWith(_.split("_")(0) < _.split("_")(0))))
      .map{ line =>
        val st = new StringBuffer()
        line._2.foreach{ array => {
          val splited = array.split("_")
          st.append(splited(2)+"->")
        }}
        (line._1,line._2(0).split("_")(1),st.toString)
      }.foreach(println)

  }
}
