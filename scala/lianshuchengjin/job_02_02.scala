package scala.lianshuchengjin

import breeze.linalg.{*, DenseVector, DenseMatrix}
import breeze.numerics._
import breeze.stats._
import org.apache.spark.{SparkContext, SparkConf}

/**
 * Created by Administrator on 2016/6/24.
 */
object job_02_02 {

  def main(args: Array[String]) {

    val conf = new SparkConf().setAppName("WordCount").setMaster("local")
    val sc = new SparkContext(conf)


    var nums = 1000;
    var features = 50
    //生成数据
    val featuresMatrix = DenseMatrix.rand[Double](nums,features)
    val labelMatrix = DenseMatrix.rand[Double](nums,1)
    //求均值和方差
    val featuresMean = mean(featuresMatrix(::, *)).toDenseVector
    val featuresStddev = stddev(featuresMatrix(::,*)).toDenseVector
    //归一化
    featuresMatrix(*,::) -= featuresMean
    featuresMatrix(*,::) /= featuresStddev
    //增加截距
    val intercept = DenseMatrix.ones[Double](featuresMatrix.rows,1)
    val train = DenseMatrix.horzcat(intercept,featuresMatrix)
    //计算
    val w = DenseMatrix.rand[Double](features+1,1)
    val A = (train * w).asInstanceOf[DenseMatrix[Double]]
    val probability = 1.0/(exp(A * -1.0) + 1.0)
    //RMSE
    val RMSE = sqrt(mean(pow(probability - labelMatrix,2)))
    println(RMSE)

  }


}
