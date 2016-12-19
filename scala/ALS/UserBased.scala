package scala.ALS

import org.apache.spark.{SparkContext, SparkConf}

/**
 * Created by Administrator on 2016/5/31.
 */
object UserBased {

  def  main (args: Array[String]){

    val conf  = new SparkConf().setMaster("local").setAppName("UserBased")
    val sc = new SparkContext(conf)

    //将评分数据读取到CoordinateMatrix中
    import org.apache.spark.mllib.linalg.distributed._
    val data = sc.textFile("E:\\softwares\\spark-1.6.0-bin-hadoop2.6\\data\\mllib\\als\\test.data")
    val parsedData = data.map(_.split(',') match {
      case Array(user,item,rate) =>MatrixEntry(user.toLong - 1, item.toLong - 1,rate.toDouble)
    })
    val ratings = new CoordinateMatrix(parsedData)

    //将CoordinateMatrix转换为RowMatrix计算两两用户的余弦相似度。由于RowMatrix只能计算列之间的相似度，而用户数据是
    //有行表示，因此CoordinateMatrix需要先计算转置：
    val matrix = ratings.transpose().toRowMatrix();
    val similarities = matrix.columnSimilarities(0.1)

    //假设需要预测用户1对项目1的评分，那么预测结果就是用户1的评分评分加上其他用户对项目1评分的按相似度的加权平均：
    //计算用户1的平均评分
    val ratingsOfUser1 = ratings.toRowMatrix().rows.toArray()(0).toArray
    val avgRatingOfUser1 = ratingsOfUser1.sum/ratingsOfUser1.size
    //计算其他用户对项目1的加权平均分
    val ratingsToItem1 = matrix.rows.toArray()(0).toArray.drop(1)
    val weights = similarities.entries.filter(_.i == 0).sortBy(_.j).map(_.value).collect()
    val weigthedR = ( 0 to 2 ).map(t => weights(t)*ratingsToItem1(t)).sum /weights.sum
    //求和输出预测结果
    printf("Rating of user 1 towords item 1 is : " + (avgRatingOfUser1 + weigthedR))
  }

}
