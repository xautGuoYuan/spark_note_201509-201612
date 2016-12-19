package scala.SparkSQL

import org.apache.spark.SparkConf
import org.apache.spark.api.java.JavaSparkContext
import org.apache.spark.sql.SQLContext
import org.apache.spark.sql.hive.HiveContext

/**
 * Created by Administrator on 2016/4/3.
 */
object SparkSQLJDBC2MySQL {

  def  main (args: Array[String]){
    val conf = new SparkConf().setMaster("local").setAppName("SparkSQLJDBC2MySQL");
    val sc = new JavaSparkContext(conf);
    val sqlContext = new SQLContext(sc);

    val reader = sqlContext.read.format("jdbc");
    reader.option("url", "jdbc:mysql://localhost:3306/spark");
    reader.option("dbtable", "momo");
    reader.option("driver", "com.mysql.jdbc.Driver");
    reader.option("user", "root");
    reader.option("password", "root");
    val momoDataSourceDFFrameFromMySQL = reader.load; //基于momo表创建DataFram
    momoDataSourceDFFrameFromMySQL.show();
  }

}
