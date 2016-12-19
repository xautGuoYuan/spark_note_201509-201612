package scala.beifengwang

/**
 * Created by Administrator on 2016/8/2.
 * 隐式转换函数的作用域和导入：
 * Scala默认会使用两种隐式转换，一种是源类型，或者目标类型的伴生对象内的隐式转换函数；一种是当前程序作用域内的可以用
 * 唯一标识符表示的隐式转换函数（以下演示的就是这种情况）
 * 如果隐式转换函数不在上述两种情况下的话，那么就必须手动使用import语法引入某个包下的隐式转换函数。通常建议，仅仅在需要
 * 进行隐式转换地方，比如某个函数或者方法内，用import导入隐式转换函数，这样可以缩小隐式转换函数的作用域，避免不需要的
 * 隐式转换。
 *
 * 隐式转换的发生时机：
 * 1，调用某个函数，但是给函数传入的参数的类型，与函数定义的接收参数类型不匹配（例如下面的特殊售票窗口）
 * 2，使用某个类型的对象，调用某个方法，而这个方法并不存在与该类型时（例如下面的超人变身）
 * 3，使用某个类型的对象，调用某个方法，虽然该类型有这个方法，但是给方法传入的参数类型，与方法定义的接受参数的类型
 * 不匹配（相当于第一种情况）（例如特殊售票窗口加强版）
 */
object Implicit_ {

  def main(args: Array[String]) {

    val specialPerson = new SpecialPerson("SpecialPerson")
    val student = new Student("Student")
    val older = new Older("Older")
    val teacher = new Teacher("Teacher")
    println(buySpecialTicket(specialPerson))
    println(buySpecialTicket(student))
    println(buySpecialTicket(older))
    //println(buySpecialTicket(teacher)) 不能成功，会返回Nil，然后循环调用隐式转换，导致死循环

    val man = new Man("GuoYuan")
    man.emitLaser

    val ticketHouse = new Tickethouse
    println(ticketHouse.buySpecialTicket(student))

    signForExam("GuoYuan")
  }

  /**
   * 隐士转换
   * 例如：特殊售票窗口（只接受特殊人群，比如学生、老人等）
   * @param name
   */
  class SpecialPerson(val name:String)
  class Student(val name:String)
  class Older(val name:String)
  class Teacher(val name:String)

  implicit def object2SpecialPerson(obj:Object):SpecialPerson = {
    if(obj.getClass == classOf[Student]){
      val stu = obj.asInstanceOf[Student]
      new SpecialPerson((stu.name))
    } else {
      if (obj.getClass == classOf[Older]) {
        val older = obj.asInstanceOf[Older]
        new SpecialPerson(older.name)
      } else
        Nil
    }
  }

  var ticketNumber = 0
  def buySpecialTicket(p:SpecialPerson) = {
    ticketNumber += 1
    "T-" + ticketNumber
  }

  /**
   * 使用隐式转换加强现有类型
   * 隐式转换非常强大的一个功能，就是可以在不知不觉中加强现有类型的功能。也就是说，可以为某个类定义一个加强版的类，
   * 并定义相互之间的隐式转换，从而让源类在使用加强版的方法时，由Scala自动进行隐式转换为加强类，然后在调用该方法。
   *
   * 例如：超人变身
   */

  class Man(val name:String)
  class Superman(val name:String){
    def emitLaser = println("emit a laster!")
  }
  implicit def man2superman(man:Man):Superman = new Superman(man.name)

  /**
   * 例如：特殊售票窗口加强版
   */
  class Tickethouse {
    var ticketNumber = 0
    def buySpecialTicket(p:SpecialPerson) = {
      ticketNumber += 1
      "T-" + ticketNumber
    }
  }

  /**
   * 隐式参数
   * 所谓的隐式参数，指的是在函数或者方法中，定义一个用implicit修饰的参数，此时Scala会尝试找到一个指定类型的，用
   * implicit修饰的对象，即隐式值，并注入参数。
   * Scala会在两个范围内查找：一种是当前作用域内可见的val或var定义的隐式变量；一种是隐式参数类型的伴生对象内的隐式值
   *
   * 例如：考试签到
   */

  class SignPen{
    def write(content:String) = println(content)
  }
  implicit val signPen = new SignPen

  def signForExam(name:String)(implicit signPen:SignPen): Unit = {
    signPen.write(name + "come to exam in time")
  }


}
































