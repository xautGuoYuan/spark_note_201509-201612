package scala.MLlib_Book

import org.apache.spark.{SparkContext, SparkConf}

/**
 * Created by Administrator on 2016/4/11.
 */
object takeSampleByKey {


  def main(args: Array[String]) {
    val conf = new SparkConf().setMaster("local").setAppName("Cartesian")
    val sc = new SparkContext(conf)
    val randRDD = sc.parallelize(List( (7,"cat"), (6, "mouse"),(7, "cup"), (6, "book"), (7, "tv"), (6, "screen"), (7, "heater")))
    val sampleMap = List((7, 1.0), (6, 1.0)).toMap
    randRDD.sampleByKey(false, sampleMap,42).collect.foreach(println)

    val data = sc.parallelize(1 to 1000000)
        .map(row => {
          if (row <= 500000)
            (1,""+row)
          else (2,""+row)
        })
    val sampleMap1 = List((1, 0.2), (2, 0.8)).toMap
    data.sampleByKey(false,sampleMap1,0).collect.foreach(println)
    data.sampleByKey(false,sampleMap1,0).countByKey().foreach(println)

    val dt = sc.parallelize(Array("1,2,3,4,5,6,7,8,9,10")).flatMap(_.split(","))
    val result = dt.sample(false,0.5,3)
    result.foreach(println)



  }

}
