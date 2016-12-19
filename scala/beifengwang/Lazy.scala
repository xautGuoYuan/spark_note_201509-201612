package scala.beifengwang

/**
 * Created by Administrator on 2016/7/31.
 */
object Lazy {

  def main(args: Array[String]) {

    import scala.io.Source._
    lazy val lines = fromFile("c:/ImbaMallLog.txt").mkString
    println(lines)
  }
}
