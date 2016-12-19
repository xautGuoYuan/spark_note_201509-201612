package scala.ML


import org.apache.spark.mllib.clustering.{KMeans, KMeansModel, GaussianMixture}
import org.apache.spark.mllib.linalg.Vectors
import org.apache.spark.{SparkContext, SparkConf}

/**
 * Created by Administrator on 2016/5/4.
 */
object GMM {

  def main(args: Array[String]) {
    val conf = new SparkConf().setMaster("local").setAppName("GMM")
    val sc = new SparkContext(conf)
    sc.setLogLevel("OFF")

    val rawTrainingData = sc.textFile("C:/Users/Administrator/Desktop/Wholesale customers data.txt")
    val parsedTrainingData = rawTrainingData.filter(!isColumnNameLine(_)).map( line =>{
      Vectors.dense(line.split(",").map(_.trim).filter(!"".equals(_)).map(_.toDouble))
    }).cache()
    var clusterIndex:Int = 0
    val cluster:KMeansModel =KMeans.train(parsedTrainingData,8,30,1)
    println("cluster Number:" + cluster.clusterCenters.length)
    println("Cluster Center Information Overview:")
    cluster.clusterCenters.foreach(
       x=>{
         println("Center Point of Cluster" + clusterIndex + ":")
         println(x)
         clusterIndex +=1
       }
    )

/*
    val rawTestData = sc.textFile("C:/Users/Administrator/Desktop/Wholesale customers data1.txt")
    val parsedTestData = rawTestData.map(line =>
    {

      Vectors.dense(line.split(",").map(_.trim).filter(!"".equals(_)).map(_.toDouble))

    })
    parsedTestData.collect().foreach(testDataLine => {
      val predictedClusterIndex: Int = cluster.predict(testDataLine)

      println("The data " + testDataLine.toString + " belongs to cluster " +
        predictedClusterIndex)
    })

    println("Spark MLlib K-means clustering test finished.")*/




    val ks:Array[Int] = Array(3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20)
    ks.foreach(cluster => {
      val model:KMeansModel = KMeans.train(parsedTrainingData, cluster,30,1)
      val ssd = model.computeCost(parsedTrainingData)
      println("sum of squared distances of points to their nearest center when k=" + cluster + " -> "+ ssd)
    })


  }

  private def
isColumnNameLine(line:String):Boolean = {
    if (line != null &&
      line.contains("Channel")) true
    else false
  }
}
