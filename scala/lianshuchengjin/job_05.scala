package scala.lianshuchengjin


import org.apache.spark.mllib.classification.{NaiveBayesModel, NaiveBayes, LogisticRegressionModel, LogisticRegressionWithSGD}
import org.apache.spark.mllib.evaluation.BinaryClassificationMetrics
import org.apache.spark.mllib.feature.{HashingTF, IDF, StandardScaler}
import org.apache.spark.mllib.linalg.Vectors
import org.apache.spark.mllib.optimization.{SimpleUpdater, SquaredL2Updater, Updater}
import org.apache.spark.mllib.regression.LabeledPoint
import org.apache.spark.mllib.tree.DecisionTree
import org.apache.spark.mllib.tree.configuration.Algo
import org.apache.spark.mllib.tree.impurity.{Gini, Entropy, Impurity}
import org.apache.spark.rdd.RDD
import org.apache.spark.{SparkConf, SparkContext}

/**
 * Created by Administrator on 2016/7/9.
 */
object job_05 {

  def main(args: Array[String]) {

    val conf = new SparkConf().setMaster("local[16]").setAppName("job_05")
    val sc = new SparkContext(conf)
    sc.setLogLevel("OFF")

    val date = "2016-03-25"
    //用户安装列表
    val data_1 = sc.textFile("file:/D:/user/*.gz") //上报日期、用户ID、安装包名
    val data_1_day_information = data_1.distinct().map(_.split("\t")).filter(x => x(0) == date).map(x => (x(1), x(2)))
    val data_2 = sc.textFile("file:/D:\\BaiduYunDownload\\spark mllib\\Spark MLlib机器学习04\\用户标签数据\\用户标签数据/*.gz") //用户ID、日期、标签值（Double类型的）
    val data_2_day_information = data_2.distinct().map(_.split("\t")).filter(x => x(1) == date).map(x => (x(0), x(2)))
    val packages = data_1_day_information.map(x => x._2).distinct().collect.zipWithIndex.toMap
    val maxPackageIdx = packages.values.toArray.max
    val record = data_1_day_information.groupByKey().join(data_2_day_information) // （用户ID，（安装列表，标签值））
    val labeledPoint_data = record.map { r =>

        /** 处理安装列表 */
        val features = Array.ofDim[Double](maxPackageIdx)
        for (_package <- r._2._1) {
          val packageIdx = packages(_package)
          features(packageIdx) = 1.0
        }
        /** 处理标签 */
        val label = if (r._2._2.toDouble < 0.5) 0 else { if ( 1>= r._2._2.toDouble &&  r._2._2.toDouble>= 0.5) 1 else 2 }
        LabeledPoint(label, Vectors.dense(features))
      }

    /** 样本划分 */
    val scaleDataSplit = labeledPoint_data.randomSplit(Array(0.6, 0.4), 123)
    val train = scaleDataSplit(0).cache()
    val test = scaleDataSplit(1)

    /**数的深度*/
    println("树的深度")
    Seq(1,2,3,4,5,10,20,30).foreach{ param =>
      val model = trainDTWithParams(train,param,Entropy)
      val scoreAndLabels = test.map{ point =>
        val score = model.predict(point.features)
        (if (score < 0.5) 0.0 else {if ( 1>= score &&  score>= 0.5) 1.0 else 2.0 },point.label)
      }
      val testErr = scoreAndLabels.filter(r => r._1 != r._2).count.toDouble/scoreAndLabels.count
      println(f"$param tree depth AND Entropy  test Error: " + testErr)
    }

    /**不纯度*/
    println("不纯度")
    Seq(1,2,3,4,5,10,20,30).foreach{ param =>
      val model = trainDTWithParams(train,param,Gini)
      val scoreAndLabels = test.map{ point =>
        val score = model.predict(point.features)
        (if (score < 0.5) 0.0 else { if ( 1>= score &&  score>= 0.5) 1.0 else 2.0 },point.label)
      }
      val testErr = scoreAndLabels.filter(r => r._1 != r._2).count.toDouble/scoreAndLabels.count
      println(f"$param tree depth AND Gini test Error: " + testErr)
    }
  }
  def trainDTWithParams( input:RDD[LabeledPoint],maxDepth:Int,impurity:Impurity) = {
    DecisionTree.train(input,Algo.Classification,impurity,maxDepth,3)
  }

}
