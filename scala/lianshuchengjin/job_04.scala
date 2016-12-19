package scala.lianshuchengjin

import org.apache.spark.mllib.classification.{LogisticRegressionModel, LogisticRegressionWithSGD}
import org.apache.spark.mllib.evaluation.BinaryClassificationMetrics
import org.apache.spark.mllib.feature.{IDF, HashingTF, PCA, StandardScaler}
import org.apache.spark.mllib.linalg.{SparseVector, Vectors, Vector}
import org.apache.spark.mllib.optimization.{SquaredL2Updater, SimpleUpdater, Updater}
import org.apache.spark.mllib.regression.{LabeledPoint}
import org.apache.spark.rdd.RDD
import org.apache.spark.{SparkContext, SparkConf}

/**
 * Created by Administrator on 2016/7/9.
 */
object job_04 {

  def main(args: Array[String]) {

    val conf = new SparkConf().setMaster("local[32]").setAppName("job_04")
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
        val label = if (r._2._2.toDouble > 0.5) 1 else 0
        LabeledPoint(label, Vectors.dense(features))
      }

    //归一化：使用SparkStandardScaler中的方法完成 x-u/sqrt(variance)
    val scaler = new StandardScaler(withMean = true, withStd = true).fit(labeledPoint_data.map(x => x.features))
    val scaledData = labeledPoint_data.map(lp => LabeledPoint(lp.label, scaler.transform(lp.features)))

    /** 样本划分 */
    val scaleDataSplit = scaledData.randomSplit(Array(0.6, 0.4), 123)
    val train = scaleDataSplit(0).cache()
    val test = scaleDataSplit(1)


     //参数选取

     var numIterations = 10
     val stepSize = 1.0
    // 迭代
    val iterResults = Seq(1,5,10,50).map{ param =>
      val model = trainWithParams(train,0.0,param,new SimpleUpdater,1.0)
      createMetrics(s"$param iteration",test,model)
    }
    iterResults.foreach{ case (param,auc) => println(f"$param ,AUC = $auc")}

    //步长
    val stepResults = Seq(0.001,0.01,0.1,1.0,10.0).map{ param =>
      val model = trainWithParams(train,0.0,numIterations,new SimpleUpdater,param )
      createMetrics(s"$param step size",test,model)
    }

    stepResults.foreach{ case (param,auc) => println(f"$param,AUC = $auc")}

    //正则化
    val regResults = Seq(0.001,0.1,1.0,10.0).map{ param =>
      val model  = trainWithParams(train,param,numIterations,new SquaredL2Updater,stepSize)
      createMetrics(s"$param L2 regularization parameter",test,model)
    }
    regResults.foreach{ case(param,auc) => println(f"$param,AUC = $auc")}

    /**
     * 使用TF-IDF模型把用户安装列表转化成向量形式表示，作为特征向量
     */

    val aa = record.map(_._2)
    val hashingTF = new HashingTF()
    val tf = hashingTF.transform(aa.map(_._1))
    val aa_tf = aa.map(x => (hashingTF.transform(x._1),x._2))
    val idf = new IDF().fit(tf)
    val tfidf = idf.transform(tf)
    val aa_tfidf = aa_tf.map(x => (idf.transform(x._1),x._2))

    val labeledPoint_tfidf = aa_tfidf.map{r =>
      val label = if(r._2.toDouble > 0.5) 1 else 0
      LabeledPoint(label,r._1)
    }
    val labeledPoint_tfidfSplit = labeledPoint_tfidf.randomSplit(Array(0.6,0.4),123)
    val train_tfidf = labeledPoint_tfidfSplit(0).cache()
    val test_tfidf = labeledPoint_tfidfSplit(1)
    val model = trainWithParams(train_tfidf,0.1,50,new SimpleUpdater,1.0 )
    val result = createMetrics("tfidf" ,test_tfidf,model)
    println(result._1 + ":     " +result._2)


  }


  def trainWithParams(input:RDD[LabeledPoint],regParam:Double,numIterations:Int,updater:Updater,stepSize:Double) = {
    val lr = new LogisticRegressionWithSGD
    lr.optimizer.setNumIterations(numIterations).setUpdater(updater).setRegParam(regParam).setStepSize(stepSize)
    lr.run(input)
  }
  /** AUC */
  def createMetrics(label:String,data:RDD[LabeledPoint],model:LogisticRegressionModel) = {
    val scoreAndLabels = data.map{ point =>
      (model.predict(point.features),point.label)
    }
    val metrics = new BinaryClassificationMetrics(scoreAndLabels)
    (label,metrics.areaUnderROC())
  }

}
