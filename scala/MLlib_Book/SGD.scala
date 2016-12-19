package scala.MLlib_Book

import scala.collection.mutable

/**
 * Created by Administrator on 2016/4/11.
 */
object SGD {

  val data = mutable.HashMap[Int,Int]()
  def getData():mutable.HashMap[Int,Int] = {
    for(i <- 1 to 50) {
      data += (i -> (2*i))
    }
    data
  }

  var w:Double = 0;
  var step:Double = 0.1

  def sgd(x:Double,y:Double) = {
    w = w - step*((w * x) - y)

  }

  def main(args:Array[String]): Unit = {
    val dataSource = getData()
    dataSource.foreach(myMap => {
      sgd(myMap._1,myMap._2)
    })
    println("最终结果w值为："+ w)
  }

}
