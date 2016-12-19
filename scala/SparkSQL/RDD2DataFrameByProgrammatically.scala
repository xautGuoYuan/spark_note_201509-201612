package scala.SparkSQL

import java.util

import org.apache.spark.sql.types.{DataTypes, StructField, StructType}
import org.apache.spark.sql.{Row, SQLContext}
import org.apache.spark.{SparkContext, SparkConf}

/**
 * Created by Administrator on 2016/3/17.
 */
object RDD2DataFrameByProgrammatically {
       def main(args: Array[String]): Unit = {
         val conf = new SparkConf().setMaster("local").setAppName("RDD2DataFrameByProgrammatically")
         val sc = new SparkContext(conf)
         val sqlContext = new SQLContext(sc)

         val lines = sc.textFile("E:/softwares/spark-1.6.0/examples/src/main/resources/person.txt")
         val personsRDD = lines.map(_.split("\t")).map(p=>Row(p(0).toInt,p(1),p(2).toInt));

         //val schemaString = "name age"
         //val schema = StructType(schemaString.split(" ").map(fieldName => StructField(fieldName,DataTypes.StringType,true)))
         import scala.collection.mutable.ListBuffer
         val structType = ListBuffer[StructField]()
         structType += StructField("id",DataTypes.IntegerType,true);
         structType += StructField("name",DataTypes.StringType,true);
         structType += StructField("age",DataTypes.IntegerType,true);
         val scheme = StructType(structType);

         val DF = sqlContext.createDataFrame(personsRDD,scheme);
         DF.show

         DF.registerTempTable("person")

         val result =  sqlContext.sql("select * from person").rdd
         result.foreach(println(_))

       }
}
