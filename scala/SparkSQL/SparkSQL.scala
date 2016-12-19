package scala.SparkSQL

import java.io.PrintWriter

import org.apache.spark.sql.SQLContext
import org.apache.spark.{SparkContext, SparkConf}

/**
 * Created by Administrator on 2016/4/24.
 */

case class  LogInf(a:String,b:String,c:String,d:Int,e:Int,f:String)
object SparkSQL {

  def main(args: Array[String]) {

    val conf = new SparkConf().setAppName("log")
    val sc = new SparkContext(conf)

    val sqlContext = new SQLContext(sc)
    import sqlContext.implicits._
    val logs_1 = sc.textFile(args(0)).map(_.split("\t")).filter(_.length == 5)
    val logs = logs_1.map(x=>Array(x(0),x(1),x(2),x(3).split(" ")(0),x(3).split(" ")(1),x(4)))
    val df = logs.map(p=>LogInf(p(0),p(1),p(2),p(3).trim.toInt,p(4).trim.toInt,p(5))).toDF()
    df.registerTempTable("logs")
    val a = sqlContext.sql("select distinct b from logs").count()
    val writer = new PrintWriter("usr/local/result.txt")
    writer.println("session:",a)
    val rdd3 = sqlContext.sql("select c from logs")
    val b = rdd3.map(_.toString).map(_.split(" ")).filter(_.length <= 3).count()
    writer.println("length of search:%d",b)
    writer.println("times of search:")
    val rdd4 = logs.map(x=>x(2)).map(x=>(x,1)).reduceByKey(_+_).map(x=>(x._2,x._1)).sortByKey(false).map(x=>
      (x._2,x._1)).take(10).foreach(writer.println)
    logs.map(x=>x(2)).map(x=>(x,1)).reduceByKey(_+_).map(x=>(x._2,x._1)).sortByKey(false).map(x=>
      (x._2,x._1)).take(10).foreach(println)
    val c = sqlContext.sql("select b,count(c) from logs group by b having count(c) =1 order by b desc").count()
    writer.println("The number of one session:",c)
    writer.println("the rank and the click:")
    sqlContext.sql("select d,count(e) from logs group by d order by d").collect().foreach(writer.println)
    writer.close()


  }

}
