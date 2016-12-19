package scala.beifengwang

import scala.collection.mutable.ArrayBuffer

/**
 * Created by Administrator on 2016/7/31.
 */
object InnerClass {

  def main(args: Array[String]) {
    val c1 = new Class
    val s1 = c1.getStudent("guoyuan")
    c1.student += s1

    val c2 = new Class
    val s2 = c2.getStudent("guomiao")
    //c1.student += s2 这个燕子会报错，原因：每个外部类的对象的内部类，都是不同的类
  }

}
class Class{
  class Student_2(val name:String) {} //内部类
  val student = new ArrayBuffer[Student_2]()
  def getStudent(name:String) = {
    new Student_2(name)
  }
}
