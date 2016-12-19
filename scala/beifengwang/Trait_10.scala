package scala.beifengwang

/**
 * Created by Administrator on 2016/8/1.
 * 在Scala中，trait是没有接受参数的构造函数的，这是trait与class的唯一区别，但是如果需求就是要trait能够对field进行初始化，
 * 该怎么办？只能使用Scala非常特殊的一种高级特性------提前定义
 * 结合trait_9来理解
 */
object Trait_10 {

  def main(args: Array[String]) {


    /**方法一*/
    val p = new {
      val msg:String = "init"
    } with P11 with SayHello_11
  }
}

/**方法一*/
trait SayHello_11{
  val msg : String
  println(msg.toString)
}
class P11

/**方法二*/
class P12 extends {
  val msg:String = "int"
} with SayHello_11

/**方法三*/
trait Sayhello_12 {
  lazy val msg:String = null
  println(msg.toString)
}
class P13 extends Sayhello_12{
  override lazy val msg:String = "init"
}