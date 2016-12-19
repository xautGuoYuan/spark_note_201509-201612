package scala.beifengwang

/**
 * Created by Administrator on 2016/8/1.
 * trait的构造机制
 * 在Scala中，trait也是有构造代码的，也就是trait中的，不包含在任何方法中的代码
 * 而继承了trait的类的构造机制如下：
 * 1、父类的构造函数执行；
 * 2、trait的构造代码执行，多个trait从左到右依次执行；
 * 3、构造trait时会先构造父trait，如果多个trait继承同一个父trait，则父trait只会构造依次；
 * 4，所有trait构造完毕之后，子类的构造函数执行
 */
object Trait_9 {

  def main(args: Array[String]) {
    val a = new S10
  }

}
class P10{println("P10's constructor!")}
trait Logger_10{println("Logger_10's constructor")}
trait MyLogger_10 extends Logger_10 {println("MyLogger's constructor")}
trait TimeLogger_10 extends Logger_10 {println("TimeLogger_10's constructor")}
class S10 extends P10 with MyLogger_10 with TimeLogger_10 {
  println("S10's constructor")
}

