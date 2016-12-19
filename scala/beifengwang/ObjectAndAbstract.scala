package scala.beifengwang

/**
 * Created by Administrator on 2016/7/31.
 * object的功能其实和class类似，除了不能定义接受参数的constructor之外
 * object也可以继承抽象类，并覆盖抽象类的方法
 */
object ObjectAndAbstract {

  def main(args: Array[String]) {
    HelloImpl.sayHello("guoyuan")
  }
}
abstract  class Hello(var message:String) {
  def sayHello(name:String):Unit
}
object HelloImpl extends Hello("hello") {
  override def sayHello(name:String) = {
    println(message + "," + name)
  }
}
