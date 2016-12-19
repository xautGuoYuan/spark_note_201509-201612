package scala.beifengwang

import org.apache.spark.sql.types._
import org.apache.spark.sql.{Row, SQLContext}
import org.apache.spark.{SparkContext, SparkConf}
import org.apache.spark.sql.functions._

/**
 * Created by Administrator on 2016/8/15.
 */
object DailyUV {

  def main(args: Array[String]) {

    val conf = new SparkConf().setMaster("local").setAppName("DailyUV")
    val sc = new SparkContext(conf)
    val sqlContext = new SQLContext(sc)
    sc.setLogLevel("OFF")

    //构造用户访问日志数据，并创建DataFrame
    val userAccessLog = Array(
      "2015-10-01,1122",
      "2015-10-01,1122",
      "2015-10-01,1123",
      "2015-10-01,1124",
      "2015-10-01,1124",
      "2015-10-02,1122",
      "2015-10-02,1121",
      "2015-10-02,1123",
      "2015-10-02,1123"
    )
    val userAccessLogRDD = sc.parallelize(userAccessLog,5)

    //转换为DF
    //RDD -> RDD<Row>
    val userAccessLogRowRDD = userAccessLogRDD
      .map(log => Row(log.split(",")(0),log.split(",")(1).toInt))
    val structType = StructType(Array(
      StructField("date",DataTypes.StringType,true),
      StructField("userid",DataTypes.IntegerType,true)
    ))
    val userAccessLogRowDF = sqlContext.createDataFrame(userAccessLogRowRDD,structType)

    //UV:每天都有很多用户来访问，但是每个用户可能每天都会访问很多次，UV指的是对用户去重以后的访问总数
    //内置函数需要导入隐士转换
    import sqlContext.implicits._
    //聚合函数的用法：
    //首先，对DataFrame调用groupBy()方法，对某一列进行分组
    //然后，调用agg()方法，第一个参数，必须，必须，传入之前在groupBy()方法中出现的字段
    //第二个参数，传入countDistinct、sum、filter等，Spark提供的内置函数
    //内置函数中，传入的参数，也是用单引号作为前缀的其他的字段
    userAccessLogRowDF.groupBy("date").agg('date,countDistinct('userid)).map(row => Row(row(1),row(2))).collect().foreach(println)
























  }

}
