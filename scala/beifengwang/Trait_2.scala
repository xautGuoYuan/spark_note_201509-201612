package scala.beifengwang

/**
 * Created by Administrator on 2016/7/31.
 * Scala中的Trait可以不是只定义抽象方法，还可以定义具体方法，此时trait更像是包含了通用工具方法的东西
 * 有一个专用的名词来形容这种情况，就是说trait的功能混入了类
 * 举例来说，trait中可以包含一些很多类都通用的功能方法，比如打印日志等等，spark中就使用了trait来定义了通用的日志打印方法
 */
object Trait_2 {
  def main(args: Array[String]) {

    val a = new P3("guo")
    val b = new P3("yuan")
    a.makeFriends(b)
  }
}
trait Logger {
  def log(message:String) = println(message)
}
class P3(val name:String) extends Logger {
  def makeFriends(p:P3): Unit ={
    println("Hi,i'm " + name + " , i'm glad to make friends with you," + p.name)
    log("makeFriends method is invoked with parameter Person[name=" + p.name + "]")
  }
}