package scala.lianshuchengjin

import breeze.linalg.DenseVector
import breeze.numerics._
import org.apache.spark.mllib.feature.StandardScaler
import org.apache.spark.mllib.linalg.{Vectors, Vector}
import org.apache.spark.mllib.regression.LabeledPoint
import org.apache.spark.mllib.util.{MLUtils, LinearDataGenerator}
import org.apache.spark.{SparkContext, SparkConf}

/**
 * Created by Administrator on 2016/6/24.
 */
object job_02_01 {

  def main(args: Array[String]) {

    val conf = new SparkConf().setAppName("WordCount").setMaster("local")
    val sc = new SparkContext(conf)

    //读取数据
    val data = MLUtils.loadLibSVMFile(sc, "E:\\softwares\\spark-1.6.0-bin-hadoop2.6\\data\\mllib/sample_libsvm_data.txt").map(x => LabeledPoint(x.label,x.features.toDense))
    val vectors = data.map(lp => lp.features)
    val data_num = data.count()
    //归一化：使用SparkStandardScaler中的方法完成 x-u/sqrt(variance)
    val scaler = new StandardScaler(withMean = true,withStd = true).fit(vectors)
    val scaledData = data.map(lp => LabeledPoint(lp.label,scaler.transform(lp.features)))
    //增加截断
    val dataAndBias = scaledData.map(lp => (lp.label, appendBias(lp.features)))
    //随机初始化权重w
    val initialWeights = DenseVector.rand(vectors.first().size+1)
    //计算
    val predictionAndLabel = dataAndBias.map(x => {
      val a = breeze.linalg.DenseVector(x._2.toArray) dot breeze.linalg.DenseVector(initialWeights.toArray)
      val t = 1.0/(1.0+exp(-1.0*a))
      (t,x._1)
    })
    //RMSE
    val loss = predictionAndLabel.map{x =>
      val err = x._1 - x._2
      err*err
    }.reduce(_+_)
    val rmse = math.sqrt(loss/data_num)
    println(s"RMSE = $rmse")

  }

  def appendBias(inputVector: Vector):Vector = {
    val outputVector = DenseVector.ones[Double](inputVector.size+1)
    outputVector(1 to -1) := DenseVector(inputVector.toArray)
    Vectors.dense(outputVector.toArray)
  }

}
