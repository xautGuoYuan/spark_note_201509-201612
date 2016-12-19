package scala.beifengwang

import scala.collection.mutable

/**
 * Created by Administrator on 2016/8/1.
 */
object Set_ {

  def main(args: Array[String]) {

    /**
     * Set代表一个没有重复元素的集合
     * 将重复元素加入Set是没有用的，比如val s = Set(1,2,3);s + 1;s+4
     * 而且Set是不保证插入顺序的，也就是说，Set中的元素是乱序的，
     */
    val s = new scala.collection.mutable.HashSet[Int]();
    s += 1; s += 2; s += 5
    println(s)//Set(1, 5, 2)

    /**LinkedHashSet会用一个链表维护插入顺序*/
    val linkedHashSet= new mutable.LinkedHashSet[Int]();
    linkedHashSet += 1; linkedHashSet += 2; linkedHashSet += 3
    println(linkedHashSet)//Set(1, 2, 3)

    /**SortedSet会自动根绝key来进行排序*/
    val sortedSet = scala.collection.mutable.SortedSet("orange","apple","banana")
    println(sortedSet)//TreeSet(apple, banana, orange)
  }
}
