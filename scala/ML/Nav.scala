package scala.ML

import org.apache.spark.ml.classification.NaiveBayes
import org.apache.spark.{SparkContext, SparkConf}

/**
 * Created by Administrator on 2016/5/8.
 */
object Nav {

  def main(args: Array[String]) {
    val conf = new SparkConf().setMaster("local[32]").setAppName("Nav")
    val sc = new SparkContext(conf)
    sc.setLogLevel("ERROR")

    val rawData = sc.textFile("C:/Users/Administrator/Desktop/baizanzan/yy.txt")//读入数据 sc.textFile("路径")
    val records = rawData.map(_.split(",")).filter(x => {
      if(x.contains(""))
        false
      else
        true
    })
    val data = records.map(record => Array(record(6))++record.slice(13,22)++record(25))
    //val filter_data = data.filter(record=>record(10).toString.toDouble==4)
    //val spam = filter_data.map(record=>{record(10)=1;record})


/*    val spam = data.filter(r => {
      val xxx = r.map(x => x.toString.toDouble).map(x => if(x<0) -1 else 0)
      if(xxx.contains(-1))
        false
      else
        true
    }).map(record=>{record(10)=1;record})*/
      val spam = data.filter(r => {
      val xxx = r.map(x => x.toString.toDouble).map(x => if(x<0) -1 else 0)
      if(xxx.contains(-1))
        false
      else
        true
    }).map(record=>{if(record(10)==3)record(10)=0 else record(10) = 1;record})

    val rawData0 = sc.textFile("C:/Users/Administrator/Desktop/baizanzan/xx.txt")
    val xx = rawData0.map(_.split("\t"))
    val unSpam = xx.map(record => Array(record(6))++record.slice(13,22)++record(25))
    //val union = spam.union(unSpam)

    import org.apache.spark.mllib.regression.LabeledPoint
    import org.apache.spark.mllib.linalg.Vectors

    val finalData = spam.map(line => LabeledPoint(line(10).toString.toDouble,
      Vectors.dense(line(0).toString.toDouble,line(1).toString.toDouble,line(2).toString.toDouble,
        line(3).toString.toDouble,line(4).toString.toDouble,line(5).toString.toDouble,line(6).toString.toDouble,
        line(7).toString.toDouble,line(8).toString.toDouble,line(9).toString.toDouble))
    )
    //finalData.foreach(x => {println(x.toString())})

    val finalDataSplit = finalData.randomSplit(Array(0.6,0.4),123)//抽样
    val finalData_train = finalDataSplit(0)
    val final_test = finalDataSplit(1)

    import org.apache.spark.mllib.classification.NaiveBayes
    val nbModel = NaiveBayes.train(finalData_train)

    import org.apache.spark.mllib.classification.SVMWithSGD
    val svmModel = SVMWithSGD.train(finalData_train,20)


    val lrTotalCorrect = final_test.map{ point =>
      if(nbModel.predict(point.features) == point.label) 1 else 0
    }.sum()
    val lrAccuracy = lrTotalCorrect/(final_test.count).toDouble
    println("NAV:" + lrAccuracy)

    val lrTotalCorrect1 = final_test.map{ point =>
      if(svmModel.predict(point.features) == point.label) 1 else 0
    }.sum()
    val lrAccuracy1 = lrTotalCorrect1/(final_test.count).toDouble
    println("SVM:" + lrAccuracy1)

    import org.apache.spark.mllib.classification.LogisticRegressionWithSGD
    val lrModel = LogisticRegressionWithSGD.train(finalData_train,20)
    val lrTotalCorrect2 = final_test.map{ point =>
      if(lrModel.predict(point.features) == point.label) 1 else 0
    }.sum()
    val lrAccuracy2 = lrTotalCorrect2/final_test.count.toDouble
    println("Log:" + lrAccuracy2)

    import org.apache.spark.mllib.evaluation.BinaryClassificationMetrics
    val metrics = Seq(nbModel,svmModel,lrModel).map{model =>
      val scoreAnedLabels = final_test.map { point =>
        (model.predict(point.features),point.label)
      }
      val metrics = new BinaryClassificationMetrics(scoreAnedLabels)
      (model.getClass.getSimpleName,metrics.areaUnderPR(),metrics.areaUnderROC())
    }
    metrics.foreach(x =>
      println(x._1+"    " + x._2 + "      " + x._3)
    )

  }

}
