package scala.beifengwang

import org.apache.spark.sql.types.{DataTypes, StructField, StructType}
import org.apache.spark.sql.{Row, SQLContext}
import org.apache.spark.{SparkContext, SparkConf}

/**
 * Created by Administrator on 2016/8/15.
 */
object DailySale {

  def main(args: Array[String]) {
    val conf = new SparkConf()
      .setMaster("local")
      .setAppName("DailySale")
    val sc = new SparkContext(conf)
    val sqlContext = new SQLContext(sc)
    sc.setLogLevel("OFF")



    val userSaleLog = Array(
      "2015-10-01,55.05,1122",
      "2015-10-01,57.38,1133",
      "2015-10-01,66.66,",
      "2015-10-02,55.05,1144",
      "2015-10-02,55.05,1155",
      "2015-10-03,66.66,1123" ,
      "2015-10-03,55.07,1135")

    val userSaleLogRDD = sc.parallelize(userSaleLog,5)
    //有效销售日志的过滤
    val filteredUserSaleLogRDD = userSaleLogRDD.filter{ log =>
      if(log.split(",").length == 3) true else false
    }
    val userSaleLogRowRDD = filteredUserSaleLogRDD.map(log =>
      Row(log.split(",")(0),log.split(",")(1).toDouble)
    )
    val structType = StructType(Array(
      StructField("date",DataTypes.StringType,true),
      StructField("sale_amout",DataTypes.DoubleType,true)
    ))
    val userSaleLogDF = sqlContext.createDataFrame(userSaleLogRowRDD,structType)

    //每日销售额
    import org.apache.spark.sql.functions._
    import sqlContext.implicits._

    userSaleLogDF.groupBy("date").agg('date,sum('sale_amout)).map(row => Row(row(1),row(2))).collect.foreach(println)


  }






















}
