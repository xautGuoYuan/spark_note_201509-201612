package scala.SparkSQL

import org.apache.spark.sql.SQLContext
import org.apache.spark.{SparkContext, SparkConf}

/**
 * Created by Administrator on 2016/3/17.
 */
case class Person( id:Int, name:String, age:Int){
  override def toString: String = (id+":"+name+":"+age)
}
object RDD2DataFrameByReflection {

      def main(args:Array[String]): Unit = {

        val conf  = new SparkConf().setAppName("RDD2DataFrameFlection").setMaster("local")
        val sc = new SparkContext(conf)
        val sqlContext = new SQLContext(sc)

        val lines = sc.textFile("E:/softwares/spark-1.6.0/examples/src/main/resources/person.txt")
        val persons = lines.map(_.split("\t"))
        import sqlContext.implicits._
        val df = persons.map(p => Person(p(0).toInt,p(1),p(2).toInt)).toDF();
        df.show();
        df.registerTempTable("person")
        val bigData = sqlContext.sql("select * from person")
        //DataFrame×ª»¯³ÉRDD
        val bigDataRDD = bigData.rdd
        bigDataRDD.map(x=>new Person(x.getAs("id"),x.getAs("name"),x.getAs("age"))).foreach(x=>println(x.name))
        bigDataRDD.map(x=>(x.getAs("id"),x.getAs("name"),x.getAs("age"))).foreach(x=>println(x._1.toString +""+x._2.toString))
        val result = bigDataRDD.map(x=>new Person(x.getAs("id"),x.getAs("name"),x.getAs("age"))).collect()
        for(p  <-  result) {
          System.out.println(p.toString)
        }

      }
}
