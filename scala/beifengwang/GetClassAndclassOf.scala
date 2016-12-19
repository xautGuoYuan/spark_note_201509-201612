package scala.beifengwang

/**
 * Created by Administrator on 2016/7/31.
 * isInstanceOf只能判断出对象是否是指定类及其子类的对象，而不能精确判断出，对象就是指定类的对象
 * 如果要求精确地判断对象就是指定类的对象，那么就只能使用getClass和classOf了
 * 对象.getClass可以精确获取对象的类，classOf[类]可以精确获取类，然后使用==操作符即可判断
 * 模式匹配相当于isInstanceOf
 */
object GetClassAndclassOf {

  def main(args: Array[String]) {

    val p:Person = new Student_3
    println(p.isInstanceOf[Person])
    println(p.getClass == classOf[Person])
    println(p.getClass == classOf[Student_3])
  }
}
class Person
class Student_3 extends  Person
