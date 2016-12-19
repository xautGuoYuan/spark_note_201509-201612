package scala.beifengwang

/**
 * Created by Administrator on 2016/7/31.
 */
object Recursion {

  def main(args: Array[String]) {
    println(sum2(1 to 5: _*))
  }

  def sum2(nums:Int*):Int = {
    if(nums.length == 0) 0
    else nums.head + sum2(nums.tail: _*)
  }
}
