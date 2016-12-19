package scala.beifengwang

/**
 * Created by Administrator on 2016/7/31.
 */
object Parameter {

  def main(args: Array[String]) {
    parameters("df",10) //说明了变长参数必须放到方法的最左边
  }

  def parameters (a:String = "dd",b:Int)= {
    println(a + " " + b)
  }
}
