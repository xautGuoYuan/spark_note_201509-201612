package scala.beifengwang

/**
 * Created by Administrator on 2016/7/31.
 *
 */
object Enum {

  def main(args: Array[String]) {
    println(Season.SPRINT)

    println(Season1.AUTUMN)
    println(Season1(0))
    println(Season1.withName("spring"))
    println(Season1.AUTUMN.id)
    println(Season1.AUTUMN.toString)

    //使用枚举object.values可以遍历枚举值
    for(ele <- Season.values) println(ele)
  }
}

/**
 * Scala没有直接提供类似于Java中的Enum这样的枚举特性，如果要实现枚举，则需要用object继承Enumeration类，并
 * 调用Value方法来初始化枚举值
 */
object Season extends Enumeration{
  val SPRINT,SUMMER,AUTUMN,WINTER = Value
}

/**
 * 还可以通过Value传入枚举值的id和name，通过id和toString可以获取；还可以通过id和name来查找枚举值
 */
object Season1 extends Enumeration {
  val SPRING = Value(0,"spring")
  val SUMMER = Value(1,"summer")
  val AUTUMN = Value(2,"autumn")
  val WINTER = Value(3,"winter")
}
