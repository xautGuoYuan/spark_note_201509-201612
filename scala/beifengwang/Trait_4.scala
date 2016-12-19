package scala.beifengwang

/**
 * Created by Administrator on 2016/8/1.
 * Scala中的Trait可以定义抽象field，而trait中的具体方法则可以基于抽象field来编写
 * 但是继承trait的类，则必须覆盖抽象field，提供具体的值
 */
object Trait_4 {

  def main(args: Array[String]) {
    val a = new P5("guo")
    val b = new P5("yuan")
    a.makeFriends(b)
  }
}

trait SayHello {
  val msg:String
  def sayHello(name:String) = println(msg + "," + name)
}
class P5(val name:String) extends SayHello {
  val msg:String = "hello"
  def makeFriends(p:P5): Unit ={
    sayHello(p.name)
    println("i'm " + name + " , I want to make friends with you!")
  }
}
