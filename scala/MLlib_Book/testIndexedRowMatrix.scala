package scala.MLlib_Book

import org.apache.derby.impl.sql.execute.IndexRow
import org.apache.spark.mllib.linalg.Vectors
import org.apache.spark.mllib.linalg.distributed.{IndexedRow, IndexedRowMatrix}
import org.apache.spark.{SparkContext, SparkConf}

/**
 * Created by Administrator on 2016/4/11.
 */
object testIndexedRowMatrix {

  def main(args: Array[String]) {
    val conf = new SparkConf().setMaster("local").setAppName("Cartesian")
    val sc = new SparkContext(conf)
    val rdd = sc.parallelize(Array("1 2 3", "4 5 6"))
      .map(_.split(" ").map(_.toDouble))
      .map(line => Vectors.dense(line))
      .map((vd) => new IndexedRow(vd.size,vd))
    val irm = new IndexedRowMatrix(rdd)
    irm.rows.foreach(println)
  }

}
