package scala.MLlib_Book

import org.apache.spark.mllib.linalg.Vectors
import org.apache.spark.mllib.linalg.Vector


/**
 * Created by Administrator on 2016/4/11.
 */
object testVector {

  def main(args: Array[String]) {

    val vd: Vector = Vectors.dense(2,0,6)//建立稠密向量
    val vd1:Vector = Vectors.dense(Array[Double](1,2,3,4,5,6))
    println(vd(2))//打印稠密矩阵的第三个值
    println(vd1(4))

    val vs: Vector = Vectors.sparse(4,Array(0,1,2,3),Array(1,5,2,7))
    println(vs(3))
    val sv1: Vector = Vectors.sparse(3, Seq((0, 1.0), (2, 3.0)))
    println(sv1(2))

  }

}
