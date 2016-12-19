package scala.lianshuchengjin.小帆的帆博客

import breeze.linalg._
import breeze.numerics._
import breeze.stats._

object Work2 {
  def main(args: Array[String]) {

    // 随机产生数据
    //    val featuresMatrix = DenseMatrix.rand[Double](3, 3)
    //    val labelMatrix = DenseMatrix.rand[Double](3, 1)

    // 测试数据
    val featuresMatrix = DenseMatrix(
      (1.0, 2.0, 3.0),
      (4.0, 5.0, 6.0),
      (7.0, 8.0, 9.0)
    )

    val labelMatrix = DenseMatrix(
      1.0,
      1.0,
      0.0
    )

    // 均值
    // DenseVector(4.0, 5.0, 6.0)
    val featuresMean = mean(featuresMatrix(::, *)).toDenseVector
    println("均值：")
    println(featuresMean)

    // 标准差
    // DenseVector(3.0, 3.0, 3.0)
    val featuresStddev = stddev(featuresMatrix(::, *)).toDenseVector
    println("\n标准差：")
    println(featuresStddev)

    // 减去均值
    /**
     * -3.0  -3.0  -3.0
     * 0.0   0.0   0.0
     * 3.0   3.0   3.0
     */
    featuresMatrix(*, ::) -= featuresMean
    println("\n减去均值：")
    println(featuresMatrix)

    // 除以标准差
    /**
     * -1.0  -1.0  -1.0
     * 0.0   0.0   0.0
     * 1.0   1.0   1.0
     */
    featuresMatrix(*, ::) /= featuresStddev
    println("\n除以标准差：")
    println(featuresMatrix)

    // 生成截距
    /**
     * 1.0
     * 1.0
     * 1.0
     */
    val intercept = DenseMatrix.ones[Double](featuresMatrix.rows, 1)
    println("\n截距：")
    println(intercept)

    // 拼接成为最终的训练集
    /**
     * 1.0  -1.0  -1.0  -1.0
     * 1.0  0.0   0.0   0.0
     * 1.0  1.0   1.0   1.0
     */
    val train = DenseMatrix.horzcat(intercept, featuresMatrix)
    println("\n训练集：")
    println(train)

    // 参数
    // 为方便检查结果，这里全部设置为1
    /**
     * 1.0
     * 1.0
     * 1.0
     * 1.0
     */
    val w = new DenseMatrix(4, 1, Array(1.0, 1.0, 1.0, 1.0))
    //    val w = DenseMatrix.rand[Double](4, 1) // 随机生成, 一定要指定类型
    println("\n参数：")
    println(w)

    /**
     * -2.0
     * 1.0
     * 4.0
     */
    // 随机生成w时，如果没有指定类型，A的计算结果虽然不会有错，但是后面将无法计算，除非通过asInstanceOf进行类型转换
    // 如果w指定了类型，那么在idea中，转换语句会是灰色的，意思是这句话没有作用，可以不写
    val A = (train * w).asInstanceOf[DenseMatrix[Double]]
    println("\nA：")
    println(A)

    /**
     * 0.11920292202211755
     * 0.7310585786300049
     * 0.9820137900379085
     */
    // Sigmoid函数
    val probability = 1.0 / (exp(A * -1.0) + 1.0)
    println("\nprobability：")
    println(probability)

    /**
     * MSE : 0.6041613548425021
     */
    val MSE = mean(pow(probability - labelMatrix, 2))
    println("\nMSE：")
    println(MSE)

    /**
     * RMSE : 0.777278170825929
     */
    val RMSE = sqrt(MSE)
    println("\nRMSE：")
    println(RMSE)
  }

}