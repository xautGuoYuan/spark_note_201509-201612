package scala

/**
 * Created by Administrator on 2016/4/21.
 */

import org.apache.spark.{SparkContext, SparkConf}
import org.apache.spark.mllib.clustering.KMeans
import org.apache.spark.mllib.linalg.Vectors
object K_means {

  def main(args: Array[String]) {

    val conf = new SparkConf().setAppName("K_means")
    val sc = new SparkContext(conf)
    // Load and parse the data
    val data = sc.textFile("/kmeans/testSet.txt")
    val parsedData = data.map(s => Vectors.dense(s.split(' ').map(_.toDouble))).cache()

    // Cluster the data into two classes using KMeans
    val numClusters = 2
    val numIterations = 20
    val clusters = KMeans.train(parsedData, numClusters, numIterations)

    // Evaluate clustering by computing Within Set Sum of Squared Errors
    val WSSSE = clusters.computeCost(parsedData)
    println("Within Set Sum of Squared Errors = " + WSSSE)

    println("Final Centers: ")
    clusters.clusterCenters.foreach(println)
  }

}
