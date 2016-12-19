package scala.beifengwang

/**
 * Created by Administrator on 2016/7/31.
 * 如果有一个class，还有一个与class同名的object，那么就称这个object是class的伴生对象，class是object的伴生类
 * 伴生类和伴生对象必须存放在一个.scala文件之中
 * 伴生类和伴生对象，最大的特点就在于，互相可以访问private field
 */
object Companion {

  private val eyeNum = 2

  def main(args: Array[String]) {
    (new Companion).say
  }

}
class Companion {
  def say = println(Companion.eyeNum) //伴生类和伴生对象，最大的特点就在于，互相可以访问private field
}
