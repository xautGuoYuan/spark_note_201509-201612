package scala.MLlib_Book

import org.apache.spark.mllib.linalg.{Vectors, Matrices}

/**
 * Created by Administrator on 2016/4/11.
 */
/**
 * 本地矩阵
 */
object testMatrix {

  def main(args: Array[String]) {
    val mx = Matrices.dense(2,3,Array(1,2,3,4,5,6))//创建一个本地矩阵
    println(mx)
  }

}
