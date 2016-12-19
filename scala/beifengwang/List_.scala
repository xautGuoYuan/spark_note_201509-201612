package scala.beifengwang

/**
 * Created by Administrator on 2016/8/1.
 * 用递归函数来给List中每个元素都加上指定前缀，并打印加上前缀的元素
 */
object List_ {

  def main(args: Array[String]) {
    decorator(List(1,2,3,4),"+")

    /**LinkedList代表一个可变的列表，使用elem(head)可以引用其头部，使用next(tail)可以引用其尾部*/
    val list = scala.collection.mutable.LinkedList(1,2,3,4,5)
    println(list.elem + "  " + list.next)
    println(list.head + "  " + list.tail)
  }

  def decorator(list:List[Int],prefix:String): Unit ={
    if(list != Nil) { //Nil相当于java里面的null
      println(prefix + list.head)
      decorator(list.tail,prefix)
    }
  }
}
