package scala.beifengwang

/**
 * Created by Administrator on 2016/8/1.
 * Scala中支持让类继承多个trait后，一次调用多个trait中的同一个方法，只要让多个trait的同一个方法中，在最后都执行super.方法即可
 * 类中调用多个trait中都有的方法时，首先会从最右边的trait的方法开始执行，然后依次往左执行，形成一个调用链条
 * 这种特性非常强大，其实就相当于设计模式中的责任链模式的一种具体实现依赖
 */
object Trait_6 {

  def main(args: Array[String]) {

    val a = new P7("Guoyuan")
    a.sayHello
  }
}
trait Handler {
  def handle(data:String){}
}
trait DataValidHandler extends Handler {
  override def handle(data:String): Unit = {
    println("check data : " + data)
    super.handle(data)
  }
}
trait SignatureValidHandler extends Handler {
  override def handle(data:String): Unit = {
    println("check signature: " + data)
    super.handle(data)
  }
}
class P7(val name:String) extends SignatureValidHandler with DataValidHandler {
  def sayHello = {
    println("Hello, " + name)
    handle((name))
  }
}