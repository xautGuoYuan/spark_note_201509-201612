package scala.ALS

import org.apache.spark.{SparkContext, SparkConf}

/**
 * Created by Administrator on 2016/6/7.
 */
object ItemBased {

  def main(args: Array[String]) {

    val conf  = new SparkConf().setMaster("local").setAppName("UserBased")
    val sc = new SparkContext(conf)

    import org.apache.spark.mllib.linalg.distributed._
    val data = sc.textFile("E:\\softwares\\spark-1.6.0-bin-hadoop2.6\\data\\mllib\\als\\test.data")
    val parsedData = data.map(_.split(',') match {
      case Array(user,item,rate) =>MatrixEntry(user.toLong - 1, item.toLong - 1,rate.toDouble)
    })
    val ratings = new CoordinateMatrix(parsedData)
    //计算Item相似度
    val similarities = ratings.toRowMatrix().columnSimilarities(0.1)
    //计算项目1的平均评分
    val ratingsOfItem1 = ratings.transpose().toRowMatrix().rows.toArray()(0).toArray
    val avgRatingOfItem1 = ratingsOfItem1.sum/ratingsOfItem1.size
    //计算用户1对其他项目的加权平均评分
    val ratingsOfUser1 = ratings.toRowMatrix().rows.toArray()(0).toArray.drop(0)
    val weigths = similarities.entries.filter(_.i == 0).sortBy(_.j).map(_.value).collect
    val weightedR = ( 0 to 2 ).map(t => weigths(t)*ratingsOfUser1(t)).sum/weigths.sum
    println("Rating of User 1 toword item 1 is :" + (avgRatingOfItem1+weightedR))
  }

}
