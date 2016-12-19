package scala.beifengwang

import org.apache.spark.sql.{SQLContext, Row}
import org.apache.spark.sql.types.{DataTypes, StructField, StructType}
import org.apache.spark.{SparkContext, SparkConf}

/**
 * Created by Administrator on 2016/8/16.
 */
object UDF {

  def main(args: Array[String]) {
    val conf = new SparkConf().setAppName("UDF").setMaster("local")
    val sc = new SparkContext(conf)
    val sqlContext = new SQLContext(sc)

    val names = Array("leo","Marry","Jack","Tom")
    val namesRDD = sc.parallelize(names,3)
    val namesRowRDD = namesRDD.map(name => Row(name))
    val structType = StructType(Array(StructField("name",DataTypes.StringType,true)))
    val namesDF = sqlContext.createDataFrame(namesRowRDD,structType)

    namesDF.registerTempTable("names")

    sqlContext.udf.register("strLen",(str:String) => str.length)

    sqlContext.sql("select name,strLen(name) from names").collect().foreach(println)

  }
}
