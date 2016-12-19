package scala.SparkSQL

import org.apache.spark.sql.hive.HiveContext
import org.apache.spark.{SparkContext, SparkConf}

/**
 * Created by Administrator on 2016/4/5.
 */
object SparkSQLWindowFunctionOps {

  def main(args: Array[String]) {

    val conf = new SparkConf().setAppName("SparkSQLWindowFunctionOps")
    val sc = new SparkContext(conf)
    val hiveContext = new HiveContext(sc)
    hiveContext.sql("use hive")
    hiveContext.sql("DROP TABLE IF EXISTS scores")
    hiveContext.sql("CREATE TABLE IF NOT EXISTS scores(name STRING,score INT) ROW FORMAT DELIMITED FIELDS TERMINATED BY ' ' LINES TERMINATED BY '\\n'")
    hiveContext.sql("LOAD DATA LOCAL INPATH '/usr/local/data/TopNGroup.txt' INTO TABLE scores")

    /**
     * 使用子查询的方式完成目标数据的提取。在目标数据内部使用窗口函数row_number进行分组排序
     * PARTITION BY：指定窗口函数分组的Key；
     * ORDER BY：分组后进行排序
     *
     */
    val result = hiveContext.sql("SELECT name,score " +
      "FROM ( SELECT name,score,row_number() OVER(PARTITION BY name ORDER BY score DESC) rank  FROM scores) sub_scores " +
      "WHERE rank <=4")
    result.show()

    hiveContext.sql("DROP TABLE IF EXISTS sortedresultScores")
    result.saveAsTable("sortedresultScores")



















  }
}
