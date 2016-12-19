package scala.beifengwang

/**
 * Created by Administrator on 2016/8/1.
 */

/**
 * 第一：Scala的语法规定，将函数复制给变量时，必须在函数后面加上空格和下划线
 * 第二：sortWith 对元素进行两两相比，进行排序
 */
object Doc {

  def main(args: Array[String]) {
    Array(3,4,2,4,6,3).sortWith((_<_)).foreach(println _ )
  }
}