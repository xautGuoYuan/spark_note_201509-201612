package scala.MLlib_Book

import org.apache.spark.mllib.linalg.Vectors
import org.apache.spark.mllib.linalg.distributed.{CoordinateMatrix, MatrixEntry}
import org.apache.spark.{SparkContext, SparkConf}

/**
 * Created by Administrator on 2016/4/11.
 */
object testCoordinateRowMatrix {

  def main(args: Array[String]) {
    val conf = new SparkConf().setMaster("local").setAppName("Cartesian")
    val sc = new SparkContext(conf)
    val rdd = sc.parallelize(Array("1 2 3", "4 5 6"))
      .map(_.split(" ").map(_.toDouble))
      .map(vue => (vue(0).toLong,vue(1).toLong, vue(2)))
      .map(vue2 => new MatrixEntry(vue2._1,vue2._2,vue2._3))

    val crm = new CoordinateMatrix(rdd)
    crm.entries.foreach(println)
  }

}
