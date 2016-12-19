package scala.lianshuchengjin

import org.apache.spark.mllib.feature.{PCA, StandardScaler}
import org.apache.spark.mllib.linalg.Vectors
import org.apache.spark.mllib.regression.{LinearRegressionModel, LinearRegressionWithSGD, LabeledPoint}
import org.apache.spark.rdd.RDD
import org.apache.spark.{SparkContext, SparkConf}

/**
 * Created by Administrator on 2016/7/2.
 */
object job_03 {

  def main(args: Array[String]) {

    val conf = new SparkConf().setMaster("local").setAppName("job_03")
    val sc = new SparkContext(conf)
    sc.setLogLevel("OFF")

    val data = sc.textFile("E:\\softwares\\spark-1.6.0-bin-hadoop2.6\\data\\mllib\\ridge-data/lpsa.data")
    val parsedData = data.map { line =>
      val parts = line.split(',')
      LabeledPoint(parts(0).toDouble, Vectors.dense(parts(1).split(' ').map(_.toDouble)))
    }

    //归一化：使用SparkStandardScaler中的方法完成 x-u/sqrt(variance)
    val vectors = parsedData.map(x => x.features)
    val scaler = new StandardScaler(withMean = true,withStd = true).fit(vectors)
    val scaledData = parsedData.map(lp => LabeledPoint(lp.label,scaler.transform(lp.features))).cache()

    //模型参数的选取
    val iterations = List(10,30,60,100)
    val stepSizes = List(0.001,0.003,0.006,0.01,0.03,0.06,0.1,0.3,0.6,1,3,6,10,13,16,20)
    var bestModel :Option[LinearRegressionModel] = None
    var bestValidationMse = Double.MaxValue
    var bestIteration = 0
    var bestStepSize = 0.0
    for(iteration <- iterations;stepSize <- stepSizes) {
      val model = LinearRegressionWithSGD.train(scaledData, iteration, stepSize)
      val validationMse = computeMse(model,scaledData)
      if(validationMse < bestValidationMse) {
        bestModel = Some(model)
        bestIteration = iteration
        bestStepSize = stepSize
        bestValidationMse = validationMse
      }
    }
    println("bestIteration : " + bestIteration + "  bestStepSize : " + bestStepSize + "  MSE ：" + bestValidationMse)

    //采用SVD进行数据降维，降维也是特征选取的一种方式。某些情况下，一些特征转化本身也是特征提取的过程。以下是采用svd降维；
    val ks = List(2,3,4,5,6,7)
    for( k <- ks ) {
      val pca = new PCA(k).fit(scaledData.map(_.features))
      val training_pca = scaledData.map(p => p.copy(features = pca.transform(p.features)))
      val bestModelAndPCA = LinearRegressionWithSGD.train(training_pca, bestIteration, bestStepSize)
      val mse = computeMse(bestModelAndPCA, training_pca)
      println("使用PCA技术计算的MSE： " + mse + " k :" + k)
    }
  }

  //定义函数计算MSE
  def computeMse (model: LinearRegressionModel,data:RDD[LabeledPoint]) : Double = {
    val valuesAndPreds = data.map { point =>
      val prediction = model.predict(point.features)
      (point.label, prediction)
    }
    val MSE = valuesAndPreds.map{case(v, p) => math.pow((v - p), 2)}.mean()
    MSE
  }

}
