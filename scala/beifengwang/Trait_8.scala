package scala.beifengwang

/**
 * Created by Administrator on 2016/8/1.
 * 在trait中，可以混合使用具体方法和抽象方法
 * 可以让具体方法依赖于抽象方法，而抽象方法则放到继承trait的类中去实现
 * 这种trait其实就是设计模式中的模块设计模式的体现
 */
object Trait_8 {

  def main(args: Array[String]) {
    val a = new P9("guoyuan")
  }
}
trait Valid {
  def getName:String
  def valid:Boolean = {
    getName == "Guoyuan"
  }
}
class P9(val name:String) extends Valid {
  println(valid)
  def getName = name
}
