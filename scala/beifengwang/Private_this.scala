package scala.beifengwang

/**
 * Created by Administrator on 2016/7/31.
 * 如果将field使用private来修饰，那么代表这个field是类私有的，在类的方法中，可以直接访问类的其他对象的private field
 * 这种情况下，如果不希望field被其他对象访问到，那么可以使用private[this]，意味着对象私有的field，只有本对象内可以访问到
 */
object Private_this {

  def main(args: Array[String]) {

    val student1 = new Student_1
    val student2 = new Student_1
    println(student1.older(student2))

    //private[this]的使用
    /**
     * 如果定义Student_1的时候使用private[this] var myAge = 0
     * 则def older(s:Student_1) = {
     *  myAge > s.myAge ----------------------此处会报错
     * }
     */
  }

}
class Student_1{
  private var myAge = 0
  def age_=(newValue:Int) {
    if(newValue > 0) myAge = newValue
    else println("illegal age!")
  }
  def age = myAge
  def older(s:Student_1) = {
    myAge > s.myAge
  }
}


