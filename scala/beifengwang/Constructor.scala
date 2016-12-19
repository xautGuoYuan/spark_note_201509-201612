package scala.beifengwang

/**
 * Created by Administrator on 2016/7/31.
 */
object Constructor {

  def main(args: Array[String]) {
    val a = new A("guoyuan",24,"nan")
  }

}

class A(var name:String){
  println("constructor -----A")
  def this(name:String,age:Int){
    this(name)
    println("constructor------------B")
  }
  def this(name:String,age:Int,sex:String){
    this(name,age)
    println("constructor-------------C")
  }
}
