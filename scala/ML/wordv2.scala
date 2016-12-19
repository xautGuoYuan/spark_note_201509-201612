package scala.ML

import org.apache.spark._
import org.apache.spark.rdd._
import org.apache.spark.SparkContext._
import org.apache.spark.mllib.feature.{Word2Vec, Word2VecModel}


/**
 * Created by Administrator on 2016/4/5.
 */
object wordv2 {
  def  main (args: Array[String]){
    val conf = new SparkConf().setAppName("SparkSQLAgg").setMaster("local")
    val sc = new SparkContext(conf)

    val input = sc.textFile("E:\\text8",4).map(line => line.split(" ").toSeq)

    val word2vec = new Word2Vec()

    val model = word2vec.fit(input)

    val synonyms = model.findSynonyms("china", 40)

    for((synonym, cosineSimilarity) <- synonyms) {
      println(s"$synonym $cosineSimilarity")
    }
  }

}
