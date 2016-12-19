package scala.beifengwang

/**
 * Created by Administrator on 2016/7/30.
 */
object Break {
  /**
   * scala没有提供类似于java的break语句。
   * 但是可以使用boolean类型变量、return或者Breaks的break函数来代替使用
   * @param args
   */
  def main(args: Array[String]) {
    var n:Int = 10
    for(c <- "hello word") {
      if (5 == n)
        scala.util.control.Breaks.break
      else {
        n -= 1
        println(c)
      }
    }
  }

}
