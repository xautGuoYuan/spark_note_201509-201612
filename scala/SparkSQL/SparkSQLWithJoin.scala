package scala.SparkSQL

import org.apache.spark.sql.types.{StructType, DataTypes, StructField}
import org.apache.spark.sql.{Row, SQLContext}
import org.apache.spark.{SparkContext, SparkConf}

/**
 * Created by Administrator on 2016/4/2.
 */
object SparkSQLWithJoin {
  def main(args: Array[String]) {
    val conf = new SparkConf().setAppName("DataFrameOps").setMaster("local")
    val sc = new SparkContext(conf)
    val sqlContext =  new SQLContext(sc)

    val peopleDF = sqlContext.read.json("E:\\softwares\\spark-1.6.0-bin-hadoop2.6\\examples\\src\\main\\resources/peoples.json")
    peopleDF.registerTempTable("peopleScores")
    val execellentScoresDF = sqlContext.sql("select name,score from peopleScores where score > 90");
    execellentScoresDF.show()
    val execellentScoresNameList = execellentScoresDF.rdd.map(row => row.getAs("name").toString).collect()

    val peopleInformations = Array("{\"name\":\"Michael\",\"age\":20}", "{\"name\":\"Andy\",\"age\":17}", "{\"name\":\"Justin\",\"age\":19}")
    val peopleInformationsRDD = sc.parallelize(peopleInformations)
    val  peopleInformationsDF = sqlContext.read.json(peopleInformationsRDD)
    peopleInformationsDF.registerTempTable("peopleInformations")
    val sqlText = new StringBuilder
    sqlText.append("select name,age from peopleInformations where name in ( ")
    for(name <- execellentScoresNameList) {
      sqlText.append("'"+name+"'" +",")
    }
    sqlText.deleteCharAt(sqlText.length - 1)
    sqlText.append(")")
    println(sqlText)
    val execellentNameAgeDF = sqlContext.sql(sqlText.toString);
    execellentNameAgeDF.show
    val result = execellentScoresDF.rdd.map(row => (row.getAs("name").toString,row.getAs("score").toString.toInt)).join(
      execellentNameAgeDF.rdd.map(row => (row.getAs("name").toString,row.getAs("age").toString.toInt))
    )
    val resultRowRDD = result.map(row => Row(row._1,row._2._1,row._2._2))
    import scala.collection.mutable.ListBuffer
    val structType = ListBuffer[StructField]()
    structType += StructField("name",DataTypes.StringType,true);
    structType += StructField("score",DataTypes.IntegerType,true);
    structType += StructField("age",DataTypes.IntegerType,true);
    val scheme = StructType(structType);

    val personsDF = sqlContext.createDataFrame(resultRowRDD,scheme)
    personsDF.show()
  }

}
