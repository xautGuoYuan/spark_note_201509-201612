package scala.beifengwang

/**
 * Created by Administrator on 2016/7/31.
 * Scala中，每个类可以有一个主constructor和任意多个辅助constructor，而每个辅助constructor的第一行都必须是调用
 * 其他辅助constructor或者主constructor；因此子类的辅助constructor是一定不可能直接调用父类的constructor的
 * 只要在子类的主constructor中调用父类的constructor，一下这种语法，就是通过子类的主构造函数来调用父类的构造函数
 * 注意！如果是父类中接受的参数，比如name和age，子类中接收时，就不要用任何val或者var来修饰了，否则会认为是子类要
 * 覆盖父类的field，如果不是父类中接受的参数，例如score，则需要用val和var来修饰，如果不修饰，则会认为是构造对象的
 * 一个参数，不会再编译时编译成对象的字段。
 */
object Constructor_extends {

  def main(args: Array[String]) {
    val b1 = new B1("guoyuan")
    println(b1.score + " " + b1.name)
  }
}
class A1(val name:String,val age:Int)
class B1(name:String,age:Int,var score:Double) extends A1(name,age) {
  def this(name:String) {
    this(name,0,0)
  }
  def this(age:Int){
    this("leo",age,0)
  }
}
