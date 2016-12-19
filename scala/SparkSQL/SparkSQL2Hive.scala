package scala.SparkSQL

import org.apache.spark.SparkConf
import org.apache.spark.api.java.JavaSparkContext
import org.apache.spark.sql.{SaveMode, SQLContext}
import org.apache.spark.sql.hive.HiveContext

/**
 * Created by Administrator on 2016/4/3.
 */
object SparkSQL2Hive {
  def  main (args: Array[String]) {
    val conf = new SparkConf().setMaster("spark://master:7077").setAppName("SparkSQL2Hive");
    val sc = new JavaSparkContext(conf);
    /**
     * 第一：通过HiveContext可以直接操作hive中的数据
     * 第二：我们可以直接通过saveAsTable的方式把DataFrame中的数据保存到Hive中
     * 第三：可以直接通过HiveContext。table方法来直接加载Hive中的二表而生成DataFrame
     */
    val hiveContext = new HiveContext(sc);//这个样子就直接可以连接上Hive
    hiveContext.sql("use hive")
    hiveContext.sql("DROP TABLE IF EXISTS people")
    hiveContext.sql("CREATE TABLE IF NOT EXISTS people(name STRING,age INT) ROW FORMAT DELIMITED FIELDS TERMINATED BY '\\t' LINES TERMINATED BY '\\n'")
    hiveContext.sql("LOAD DATA LOCAL INPATH '/usr/local/data/people.txt' INTO TABLE people")

    hiveContext.sql("DROP TABLE IF EXISTS peoplescores")
    hiveContext.sql("CREATE TABLE IF NOT EXISTS peoplescores(name STRING,score INT) ROW FORMAT DELIMITED FIELDS TERMINATED BY '\\t' LINES TERMINATED BY '\\n'")
    hiveContext.sql("LOAD DATA LOCAL INPATH '/usr/local/data/peoplescore.txt' INTO TABLE peoplescores")

    /**
     * 通过HiveContext使用jlin直接基于Hive的两张表进行操作
     */
    val resultDF = hiveContext.sql("SELECT pi.name,pi.age,ps.score FROM people pi " +
      "JOIN peoplescores ps ON pi.name = ps.name where ps.score>90")
    //hiveContext.sql("DROP TABLE　IF EXISTS peopleInformationresult")
    resultDF.saveAsTable("peopleInformationresult",SaveMode.Overwrite)

    hiveContext.table("peopleInformationresult").show()
















































  }
}
