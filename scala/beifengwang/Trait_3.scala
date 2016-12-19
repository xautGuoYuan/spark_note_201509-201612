package scala.beifengwang

/**
 * Created by Administrator on 2016/8/1.
 * Scala中的Trait可以定义具体field，此时继承trait的类就自动获得了trait中定义的field
 * 但是这种获取field的方式与继承class是不同的；如果是继承class获取的field，实际是定义在父类中，子类仅仅有一个引用；
 * 而继承trait获取的field，就直接被添加到了类中
 */
object Trait_3 {

  def main(args: Array[String]) {

    val a = new S4("guoyuan")
    a.sayHello
  }
}

trait P4{
  val eyeNum:Int = 2
}
class S4(val name:String) extends P4{
  def sayHello = println("Hi,i'm " + name + " , i have " + eyeNum + " eyes.")
}

