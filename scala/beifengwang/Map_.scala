package scala.beifengwang

import scala.collection.immutable.SortedMap
import scala.collection.mutable

/**
 * Created by Administrator on 2016/7/31.
 */
object Map_ {

  def main(args: Array[String]) {

    val hashMap = new mutable.HashMap[String,Int]()
    hashMap("a") = 10
    hashMap("b") = 20
    hashMap += ("c" -> 30)
    println(hashMap) //Map(b -> 20, a -> 10, c -> 30)

    val sortedMap = SortedMap[String,Int] ("a"->10,"b"->20,"c"->30)
    println(sortedMap)////Map(a -> 10, b -> 20, c -> 30)

    val linkedHashMap = new mutable.LinkedHashMap[String,Int]()
    linkedHashMap("c") = 30
    linkedHashMap("a" ) = 10
    linkedHashMap("b" ) = 20
    println(linkedHashMap)//Map(c -> 30, a -> 10, b -> 20)
  }

}
