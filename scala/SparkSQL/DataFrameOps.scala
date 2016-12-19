package scala.SparkSQL

import org.apache.spark.sql.SQLContext
import org.apache.spark.{SparkContext, SparkConf}

/**
 * Created by Administrator on 2016/3/15.
 */
object DataFrameOps {
  def main(args: Array[String]) {

    val conf = new SparkConf().setAppName("DataFrameOps").setMaster("local")
    val sc = new SparkContext(conf)

    val sqlContext = new SQLContext(sc);

    val df = sqlContext.read.json("E:/softwares/spark-1.6.0/examples/src/main/resources/people.json");

    df.show()
    df.printSchema()
    df.select("name").show()
    df.select(df("name"), df("age") + 10).show()
    df.filter(df("age") > 10).show()
    df.groupBy("age").count.show()


  }
}
