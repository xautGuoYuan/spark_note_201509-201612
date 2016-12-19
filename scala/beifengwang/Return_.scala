package scala.beifengwang

/**
 * Created by Administrator on 2016/8/1.
 */
object Return_ {

  def main(args: Array[String]) {
    println(greeting("GuoYuan"))
  }

  def greeting(name:String) = {
    def sayHello(name:String):String = {
      return "Hello, " + name
    }
    sayHello(name)
  }

}

