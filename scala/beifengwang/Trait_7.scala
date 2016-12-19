package scala.beifengwang

/**
 * Created by Administrator on 2016/8/1.
 * 在trait中，是可以覆盖父trait的抽象方法的
 * 但是覆盖时，如果使用了super.方法的代码，则无法通过编译。因为super.方法就会去调用父trait的抽象方法，此时子trait的
 * 该方法还是会被认为是抽象的
 * 此时如果要通过编译，就得给子trait的方法加上abstract override修饰
 */
object Trait_7 {

  def main (args: Array[String]){

  }
}

trait Logger_1 {
  def log(msg:String)
}
trait MyLogger_1 extends Logger {
  abstract override def log(msg:String){
    super.log(msg)
  }
}
