package scala.ML

import org.apache.spark.mllib.classification.SVMWithSGD
import org.apache.spark.mllib.evaluation.BinaryClassificationMetrics
import org.apache.spark.mllib.linalg.Vectors
import org.apache.spark.mllib.optimization.{SquaredL2Updater, SimpleUpdater}
import org.apache.spark.mllib.regression.LabeledPoint
import org.apache.spark.rdd.RDD
import org.apache.spark.{SparkContext, SparkConf}

/**
 * Created by Administrator on 2016/4/19.
 */
object SVM_test {

  def main(args: Array[String]) {
    val conf = new SparkConf().setMaster("local").setAppName("SVM_test")
    val sc = new SparkContext(conf)

    val rawData = sc.textFile("E:/train.tsv")
    val records = rawData.map(_.split("\t"))
    val categories = records.map(r=>r(3)).distinct.collect.zipWithIndex.toMap
    val numCategories = categories.size
    val dataCategories = records.map{ r=>
      val trimmed = r.map(_.replaceAll("\"",""))
      val label = trimmed(r.size-1).toInt
      val categoryIdx = categories(r(3))
      val categoryFeatures = Array.ofDim[Double](numCategories)
      categoryFeatures(categoryIdx) = 1.0
      val otherFeatures = trimmed.slice(4,r.size-1).map(d =>if
      (d=="?") 0.0 else d.toDouble)
      val features = categoryFeatures ++ otherFeatures
      LabeledPoint(label,Vectors.dense(features))
    }
    val scalerCats = new org.apache.spark.mllib.feature.StandardScaler(withMean=true,withStd = true).
      fit(dataCategories.map(lp=>lp.features))
    val scaledDataCats = dataCategories.map(lp=>
      LabeledPoint(lp.label,scalerCats.transform(lp.features)))

    val trainTestSplit = scaledDataCats.randomSplit(Array(0.6,0.4),123)
    val train = trainTestSplit(0)
    train.cache()
    val test = trainTestSplit(1)

    val lrModel = trainWithParams(train,0.01,10,new SquaredL2Updater,1.0)
    val result = createMetrics(test,lrModel)
    println("ROC:"+result._1+":"+result._2)
    sc.stop()
  }
  import org.apache.spark.mllib.optimization.Updater
  def trainWithParams(input:RDD[LabeledPoint],regParam:Double,numIterations:Int,
                      updater:Updater,stepSize:Double) = {
    val lr = new SVMWithSGD
    lr.optimizer.setNumIterations(numIterations).setUpdater(updater).
      setStepSize(stepSize).setRegParam(regParam)
    lr.run(input)
  }
  import org.apache.spark.mllib.classification.ClassificationModel
  def createMetrics(data:RDD[LabeledPoint],model:ClassificationModel) = {
    val scoreAndLabels = data.map{point =>
      (model.predict(point.features),point.label)
    }
    val metrics = new BinaryClassificationMetrics(scoreAndLabels)
    (model.getClass.getSimpleName,metrics.areaUnderROC)
  }


}
