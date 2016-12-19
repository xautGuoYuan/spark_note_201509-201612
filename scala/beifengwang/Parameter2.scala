package scala.beifengwang

/**
 * Created by Administrator on 2016/7/31.
 */
object Parameter2 {

  def main(args: Array[String]) {
    println(sum(1,2,3,4))
    println(sum(1 to 100: _*))
  }

  def sum(nums:Int*) = {
    var result = 0
    for(num <- nums) {
      result += num
    }
    result
  }
}
