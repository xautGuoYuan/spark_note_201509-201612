package scala.lianshuchengjin

import org.apache.spark.rdd.RDD
import org.apache.spark.{SparkContext, SparkConf}

/**
 * Created by Administrator on 2016/6/11.
 */
object xxx {

  def main(args: Array[String]) {

    val conf = new SparkConf().setAppName("WordCount").setMaster("local")
    val sc = new SparkContext(conf)

    val input = sc.textFile("file:/D:/user/*.gz")
    val data = input.map(_.split("\t"))
    //数据的总数量
    println("数据总数 : " + data.count())
    //用户的数量
    val user_count = data.map(x => x(1)).distinct().count()
    println("用户的数量 : " + user_count)
    //天数
    val date_count = data.map(x => x(0)).distinct().count()
    val date = data.map(x => x(0)).distinct().collect()
    println("含有 " + date_count + "天的数据")
    println("分别是：")
    date.foreach{ x =>
      println(x)
    }

    add(data,"2016-03-25","2016-03-26").take(2).foreach{x => {
      println("用户： " + x._1+"新增软件如下：")
      x._2.foreach{ x => {
        println(x)
      }}
    }}

    println("支持度为0.1，置信度为0.2的结果如下（取前五个）")
    oneDayInformation(input,0.1,0.2,"2016-03-25").take(5).foreach(x => {
      println(x._1._1+"(A)和"+x._1._2+"(B) 支持度为： "+x._2+" 商品A->B的置信度为： "+x._3+" 商品B->A的置信度为： "+x._4)
    })
    println("支持度为0.1，置信度为0.4的结果如下（取前五个）")
    oneDayInformation(input,0.1,0.4,"2016-03-25").take(5).foreach(x => {
      println(x._1._1+"(A)和"+x._1._2+"(B) 支持度为： "+x._2+" 商品A->B的置信度为： "+x._3+" 商品B->A的置信度为： "+x._4)
    })

    /*
    指定任意1天的数据，根据每个用户的安装列表，统计每个包名的安装用户数量，由大到小排序，并且取前1000个包名，最后计算这1000个包名之间的支持度和置信度
    上报日期、用户ID、安装包名
     */

/*    val top1000 = input.distinct().map(_.split("\t")).filter(x => x(0) == "2016-03-25").map(x => (x(2),1)).reduceByKey(_+_).map(x => (x._2,x._1)).sortByKey(false).map(x => (x._2,x._1))
    .zipWithIndex().filter(x => x._2 <1000).map(x => x._1._1)
    val item = top1000.cartesian(top1000).filter(x => x._1 != x._2)*/
/*    val minCount = 100
    val dd = math.ceil(0.1*1000).toLong
    println(dd)
    val day_information = input.distinct().map(_.split("\t")).filter(x => x(0) == "2016-03-25");
    val itemAndRank = day_information.map(x => (x(2),1)).reduceByKey(_+_).map(x => (x._2,x._1)).sortByKey(false).map(x => (x._2,x._1))
    val itemToRank = itemAndRank.take(1000).toMap
    val top1000 = itemAndRank.zipWithIndex().filter(x => x._2 <1000).map(x => x._1._1).toArray()
    //val item = top1000.cartesian(top1000).filter(x => x._1 != x._2)
    val table = day_information.map(x => (x(1),x(2))).groupByKey()
    val table_count = table.count()
    val itemsets = table.filter(x=>x._2.toArray.length>=2).flatMap(x=>{
      var a = scala.collection.mutable.ArrayBuffer[(String,String)]()
      val items = x._2.toArray
      val items_count = items.length
      for(i <- 0 to items_count-2;j <- (i+1) to items_count-1){
        if(top1000.contains(items(i))&&top1000.contains(items(j))){
          a += Tuple2( items(i) , items(j) )
        }
      }
      a.toSeq
    }).filter(x => {if(x._1.isEmpty||x._2.isEmpty) false else true}).map(x => (x,1)).reduceByKey(_+_).map(x => (x._2,x._1)).sortByKey(false).map(x => (x._2,x._1))
    .map(x => ((x._1._1,x._1._2),x._2.toDouble/table_count,x._2.toDouble/itemToRank(x._1._1),x._2.toDouble/itemToRank(x._1._2)))
    itemsets.take(10).foreach(x => {
     println(x._1._1+"-------"+x._1._2+"-------"+x._2+"----->"+x._3+"--------->"+x._4)
    })*/

  }

  def oneDayInformation(input:RDD[String],minSupport:Double,minConfidence:Double,date:String):RDD[((String,String),Double,Double,Double)] = {
    val day_information = input.distinct().map(_.split("\t")).filter(x => x(0) == date)
    val itemAndRank = day_information.map(x => (x(2),1)).reduceByKey(_+_).map(x => (x._2,x._1)).sortByKey(false).map(x => (x._2,x._1))
    val itemToRank = itemAndRank.take(1000).toMap
    val top1000 = itemAndRank.zipWithIndex().filter(x => x._2 <1000).map(x => x._1._1).toArray()
    val table = day_information.map(x => (x(1),x(2))).groupByKey()
    val table_count = table.count()
    val itemsets = table.filter(x=>x._2.toArray.length>=2).flatMap(x=>{
      var a = scala.collection.mutable.ArrayBuffer[(String,String)]()
      val items = x._2.toArray
      val items_count = items.length
      for(i <- 0 to items_count-2;j <- (i+1) to items_count-1){
        if(top1000.contains(items(i))&&top1000.contains(items(j))){
          a += Tuple2( items(i) , items(j) )
        }
      }
      a.toSeq
    }).filter(x => {if(x._1.isEmpty||x._2.isEmpty) false else true}).map(x => (x,1)).reduceByKey(_+_)
      .map(x => ((x._1._1,x._1._2),x._2.toDouble/table_count,x._2.toDouble/itemToRank(x._1._1),x._2.toDouble/itemToRank(x._1._2)))
    val result = itemsets.filter(x => x._2>=minSupport&&x._3>=minConfidence&&x._4>=minConfidence)
    result
  }

  def add(data:RDD[Array[String]],date1:String,date2:String):RDD[(String,Iterable[String])] = {
    val date1_data = data.filter(x =>x(0) == date1).map(x => ( x(1),x(2)) )
    val date2_data = data.filter(x =>x(0) == date2).map(x => ( x(1),x(2)) )
    val add_data = date2_data.subtract(date1_data).distinct()
    add_data.groupByKey()
  }

}
