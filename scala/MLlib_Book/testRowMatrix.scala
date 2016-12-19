package scala.MLlib_Book

import org.apache.spark.mllib.linalg.Vectors
import org.apache.spark.mllib.linalg.distributed.RowMatrix
import org.apache.spark.{SparkContext, SparkConf}

/**
 * Created by Administrator on 2016/4/11.
 */
/**
 * 一般来说，采用分布式矩阵进行存储的情况都是数据量非常大的，其处理速度和效率与其存储格式息息相关。
 * MLlib提供了四种分布式矩阵存储形式，均有支持长整形的行列数和双精度浮点型的数据内容构成。
 * 这四种矩阵分别为：行矩阵，带有索引的行矩阵，坐标矩阵和快矩阵。
 */

/**
 * 行矩阵是最基本的一种矩阵类型。行矩阵是以行作为基本方向的矩阵存储格式，列的作用相对较小。
 * 可以将其理解为行矩阵是一个巨大的特征向量的集合。每一行就是一个具有相同格式的向量数据，
 * 且每一行的向量内容都可以单独取出来进行操作。
 */
object testRowMatrix {

  def main(args: Array[String]) {
    val conf = new SparkConf().setMaster("local").setAppName("Cartesian")
    val sc = new SparkContext(conf)
    val rdd = sc.parallelize(Array("1 2 3","4 5 6"))
      .map(_.split(" ").map(_.toDouble))
      .map(line => Vectors.dense(line))
    val rm = new RowMatrix(rdd)
    println(rm.numRows())
    println(rm.numCols())
    rm.rows.foreach(println)
  }

}
