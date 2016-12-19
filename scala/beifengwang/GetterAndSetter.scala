package scala.beifengwang

/**
 * Created by Administrator on 2016/7/31.
 */
object GetterAndSetter {

  def main(args: Array[String]) {
    val guoyuan = new Student
    println(guoyuan.name)
    guoyuan.name = "xxx"
    println(guoyuan.name)
  }

}

class Student {
  private var myName = "guoyuan"
  def name = "your name is " + myName
  def name_=(newValue:String): Unit = {
    println("you cannot edit your name")
  }
}

