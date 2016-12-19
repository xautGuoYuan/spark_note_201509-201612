package scala.lianshuchengjin

import breeze.linalg._
import org.apache.spark.{SparkContext, SparkConf}

/**
 * Created by Administrator on 2016/6/24.
 */
object learning_02 {

  def main(args: Array[String]) {

    val conf = new SparkConf().setAppName("WordCount").setMaster("local")
    val sc = new SparkContext(conf)
    sc.setLogLevel("OFF")

    //3.1.1 Breeze 创建函数
    val m1 = DenseMatrix.zeros[Double](2,3)
    val v1 = DenseVector.zeros[Double](3)
    val v2 = DenseVector.ones[Double](3)
    val v3 = DenseVector.fill(3){5.0}
    val v4 = DenseVector.range(1,10,2)
    val m2 = DenseMatrix.eye[Double](3)
    val v6 = diag(DenseVector(1.0,2.0,3.0))
    val m3 = DenseMatrix((1.0,2.0),(3.0,4.0))
    val v8 = DenseVector(1,2,3,4)
    val v9 = DenseVector(1,2,3,4).t
    val v10 = DenseVector.tabulate(3){i => i*2}
    val m4 = DenseMatrix.tabulate(3,2){case(i,j) => i+j}
    val v11 = DenseVector(Array(1,2,3,4))
    //val v11 = new DenseVector(Array(1,2,3,4))
    val m5 = new DenseMatrix(2,3,Array(11,12,13,21,22,23))
    val v12 = DenseVector.rand(4)
    val m6 = DenseMatrix.rand(2,3)

    //3.1.2 Breeze 元素访问及操作函数
    //元素访问
    val a = DenseVector(1,2,3,4,5,6,7,8,9,10)
    a(0)
    a(1 to 4 )
    a(5 to 0 by -1)
    a(1 to -1)
    a(-1)
    val m = DenseMatrix((1.0,2.0,3.0),(3.0,4.0,5.0))
    m(0,1)
    m(::,1)

    //元素操作
    val m_1 = DenseMatrix((1.0,2.0,3.0),(3.0,4.0,5.0))
    m_1.reshape(3,2)
    m_1.toDenseVector

    val m_3 = DenseMatrix((1.0,2.0,3.0),(4.0,5.0,6.0),(7.0,8.0,9.0))
    lowerTriangular(m_3)
    upperTriangular(m_3)
    m_3.copy
    diag(m_3)
    m_3(::,2) := 5.0
    m_3(1 to 2, 1 to 2) := 5.0

    val a_1 =  DenseVector(1,2,3,4,5,6,7,8,9,10)
    a_1(1 to 4) := 5
    a_1(1 to 4) := DenseVector(1,2,3,4)
    val a1 = DenseMatrix((1.0,2.0,3.0),(4.0,5.0,6.0))
    val a2 = DenseMatrix((1.0,1.0,1.0),(2.0,2.0,2.0))
    DenseMatrix.vertcat(a1,a2)
    DenseMatrix.horzcat(a1,a2)
    val b1 = DenseVector(1,2,3,4)
    val b2 = DenseVector(1,1,1,1)
    DenseVector.vertcat(b1,b2)

    //3.1.3 Breeze 数值计算函数
    println(DenseVector.vertcat(b1,b2))
  }
}
