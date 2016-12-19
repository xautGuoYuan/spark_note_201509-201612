package scala.beifengwang

/**
 * Created by Administrator on 2016/7/31.
 * 把trait作为接口来使用
 */
object Trait_1 {
  def main(args: Array[String]) {

    val p = new P1("guo")
    val p2 = new P1("jack")
    p.sayhello("yuan")
    p.makeFriends(p2)
  }

}
trait Sayhello{
  def sayhello(name:String)
}
trait MakeFriends {
  def makeFriends(p:P1)
}
class P1(val name:String)extends Sayhello with MakeFriends {
  override  def sayhello(otherName:String) = println("Hello," + otherName + ",I'm" + name)
  override  def makeFriends(p:P1) = println("Hello " + p.name + ", I'm " + name + "i want to make friends with you.")
}


















