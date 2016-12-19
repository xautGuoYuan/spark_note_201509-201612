package scala.SparkSQL

import org.apache.spark.sql.hive.HiveContext
import org.apache.spark.sql.types._
import org.apache.spark.sql.{Row, SQLContext}
import org.apache.spark.{SparkContext, SparkConf}

/**
 * Created by Administrator on 2016/4/5.
 */

/**
 * 使用Spark SQL中的内置函数对数据进行分析，Spark SQL API不同的是，DataFrame中的内置函数操作的结果返回的是一个Column对象
 *
 * 五大基本类型：
 * 1，聚合函数，例如countDistinct，sumDistinct等
 * 2，集合函数，例如sort_array,explode等
 * 3，日期，时间函数，例如hour,quarter,next_day
 * 4，数学函数，例如asin，atan，sqrt，tan，round等
 * 5，开窗函数，例如rawNumber等
 * 6，字符串函数，concat，format_number,rexexp_extract
 * 7，其他函数，isNaN，sha，randn，callUDF
 */
object SparkSQLAgg {

  def main(args: Array[String]) {

    val conf = new SparkConf().setAppName("SparkSQLAgg").setMaster("local")
    val sc = new SparkContext(conf)
    val sqlContext = new SQLContext(sc)

    val userData = Array(
      "2016-3-27,001,http://spark.apache.arg/",
      "2016-3-27,001,http://hadoop.apache.arg/",
      "2016-3-27,002,http://flink.apache.arg/",
      "2016-3-28,003,http://kafka.apache.arg/",
      "2016-3-28,004,http://spark.apache.arg/",
      "2016-3-28,002,http://hive.apache.arg/",
      "2016-3-28,001,http://parquet.apache.arg/",
      "2016-3-28,001,http://spark.apache.arg/"
    )
    val userDataRDD = sc.parallelize(userData)

    /**
     * 对数据进行预处理，生成DataFrame，要想把RDD转换成DataFrame，需要先把RDD中的元素类型变成Row类型，
     * 与此同时要提供DataFrame中的Colums的元数据描述、
     */

    val userDataRDDRow = userDataRDD.map(row => {val splited = row.split(",");Row(splited(0),splited(1).toInt,splited(2))})
    val structTypes = StructType(Array(
      StructField("time",DataTypes.StringType,true),
      StructField("id",DataTypes.IntegerType,true),
      StructField("url",DataTypes.StringType,true)
    ))

    val  userDataDF = sqlContext.createDataFrame(userDataRDDRow,structTypes)

    /**
     * 使用Spark SQl提供的内置函数对DataFra进行操作，特别注意，内置函数生成的Column对象且自动进行CG
     */
    import org.apache.spark.sql.functions._
    import sqlContext.implicits._ //要使用Spark SQL的内置函数就一定要导入SQLContext下的隐士转换
    userDataDF.groupBy("time").agg('time,countDistinct('id)).show()




































  }
}
