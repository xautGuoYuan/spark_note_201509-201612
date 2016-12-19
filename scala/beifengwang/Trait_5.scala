package scala.beifengwang

/**
 * Created by Administrator on 2016/8/1.
 * 有时我们可以在创建类的对象时，指定该对象混入某个trait，这样，就只有这个对象混入该trait的方法，而类的其他对象则没有
 */
object Trait_5 {

  def main(args: Array[String]) {

    val a = new P6("Guo")
    a.sayHello
    val b = new P6("Yuan") with MyLogger
    b.sayHello
  }

}
trait Logged {
  def log(msg:String) {}
}
trait MyLogger extends Logged {
  override def log(msg:String): Unit = {
    println("log: " + msg)
  }
}
class P6(val name:String) extends Logged {
  def sayHello: Unit = {
    println("Hi,i'm" + name)
    log("sayHello is invoked!")
  }
}