package scala.beifengwang

/**
 * Created by Administrator on 2016/7/31.
 * 匿名子类，也就是说，可以定义一个类的没有名字的子类，并直接创建其对象，然后将对象的引用
 * 赋予一个变量。之后甚至可以将该匿名类的对象传递给其他函数。
 */
object Anonymous {

  def main(args: Array[String]) {
    val p = new A2("guoyuan") {
      override def sayHello = "Hi,i'm "+ name
    }
    greeting(p)
  }
  def greeting(p:A2{def sayHello:String}): Unit = {
    println(p.sayHello)
  }

}
class A2(protected val name:String) {
  def sayHello = "Hello,i'm " + name
}
