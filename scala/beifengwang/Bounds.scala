package scala.beifengwang

/**
 * Created by Administrator on 2016/8/1.
 * 在指定泛型类型的时候，有时，我们需要对泛型类型的范围进行界定，而不是刻意是任意的类型。比如，我们可能要求某个泛型类型，
 * 它就必须是某个类的子类，这样在程序中就可以放心地调用泛型类型继承的父类的方法，程序才能正常的使用和运行。此时就可以使用
 * 上下边界Bounds的特性。
 * Scala的上下边界特性允许泛型类型必须是某个类的子类，或者必须是某个类的父类
 */
object Bounds {

  def main(args: Array[String]) {

    val a = new Student("A")
    val b = new Person("B")
    val party = new Party(a,b)
    party.play

    getLostIDCard(b)
    val jack = new Father("jack")
    val xx = new Child("xx")
    getLostIDCard(jack)
    getLostIDCard(xx)

    val dog = new Dog("XiaoHuang")
    new Party2(a,dog)//成功运行，不报错

    val calculator = new Calculator(1,2)
    println(calculator.max)

    val gongbaojiding = new Meat("gongbaojiding")
    val yuxiangrousi = new Meat("yuxiangrousi")
    val meatPackage = packageFood(gongbaojiding,yuxiangrousi)

    val card_Master = new Card[Master]("Master")
    val card_Professional = new Card[Professional]("Professional")
    enterMeet(card_Master)
    enterMeet(card_Professional)

    val card1_Master = new Card1[Master]("Master")
    val card1_Professional = new Card1[Professional]("Professional")
    enterMeet1(card1_Master)
    enterMeet1(card1_Professional)

  }


  /**上边界*/
  class Person(val name:String) {
    def sayHello = println("Hello,i'm " + name)
    def makeFriends(p:Person): Unit = {
      sayHello
      p.sayHello
    }
  }

  class Student(name:String) extends Person(name)

  class Party[T <: Person](p1:T,p2:T){ //上边界的使用
    def play = p1.makeFriends(p2)
  }

  /**下边界Bounds
    * 除了制定泛型类型的上边界，还可以指定下边界，即指定泛型类型必须是某个类的父类
    * 例如：到公安局领取身份证，可以是本人自己去，也可以是他的父亲过去领取
    * */

  class Father(val name:String)

  class Child(name:String) extends Father(name)

  def getLostIDCard[T >: Child](p:T): Unit = {
    if(p.getClass == classOf[Child]) println("please tell us your parents names")
    else if(p.getClass == classOf[Father]) println("please sign your name to get your child's lost id card.")
    else println("sorry,you are not allowed to get this id card.")
  }

  /**
   * 上下边界Bounds，虽然可以让一种泛型类型，支持有父子关系的多种类型。但是，在某个类与上下边界Bounds指定的父子
   * 类型范围内的类都没有任何关系，则默认是肯定不能接受的。
   * 然而，View Bounds作为一种上下边界Bounds的加强版，支持可以对类型进行隐式转换，将指定的类型进行隐式转换，再判断
   * 是否在边界的类型范围内*/
  class Dog(val name:String){
    def sayHello = println("Wang,Wang,i'm " + name)
  }
  implicit def dog2person(obj:Object):Person = {
    if(obj.isInstanceOf[Dog]) {
      val dog = obj.asInstanceOf[Dog]
      new Person(dog.name)
    } else
      Nil
  }
  class Party2[T <% Person](p1:T,p2:T)

  /**
   * Context Bounds是一种特殊的Bounds，它会根据泛型类型的声明，比如“T：类型”要求必须存在一个类型为“类型【T】”的
   * 隐式值。其实个人认为，Context Bounds之所以叫Context，是因为它基于的是一种全局的上下文，需要使用到上下文的
   * 隐式值以及注入。
   *
   * 例如：使用Scala内置的比较器比较大小
   */

  class Calculator[T:Ordering](val number1:T,val number2:T) {
    def max(implicit order:Ordering[T]) = if(order.compare(number1,number2)>0) number1 else number2
  }

  /**
   * 在Scala中，如果要实例化一个泛型数组，就必须使用Manifest Context Bounds。也就是说，如果数组元素类型为T的话，需要为
   * 类或者函数定义【T:Manifest】泛型类型（其实这个就相当于Context Bound的一个特例），这样才能实例化Array[T]这种泛型数组
   *
   * 例如：打包饭菜（一种食品打成一包）
   * */

  class Meat(val name:String)

  class Vegetable(val name:String)

  def packageFood[T:Manifest](food:T*) = {
    val foodPackage = new Array[T](food.length)
    for(i <- 0 until food.length) foodPackage(i) = food(i)
    foodPackage
  }

  /**
   *
   * 例如：进入会场
   */

  class Master
  class Professional extends Master
  //大师以及大师级别一下的名片都可以进入会场
  class Card[+T](val name:String) //这句话什么意思呢？例如 val a = new Card[Master]("A")
  //val b = new Card[Propfessional]("B) ,因为Master是Professional的父类，所以Card【Master】
  //也是Card【Professional】的父类，这就是协变,如果定义为class Card[T](val name:String)，则调用下面的
  //方法时，a正确，b就错误了

  def enterMeet(card:Card[Master]): Unit ={
    println("welcome to have this meeting!")
  }

  //逆变
  class Card1[-T](val name:String)
  def enterMeet1(card:Card1[Professional]): Unit ={
    println("welcome to have this meeting!")
  }

  /**
   * 协变和逆变总结一下；
   * 举例说明：
   * 如果Master是Professional的父类，并且Card[Master]是Card[Professional]的父类，这就是协变
   * 如果Master是Professional的父类，并且Card[Master]是Card[Professional]的子类，这就是协变
   */

  /**
   * 在Scala里，有一种特殊的类型参数，就是Existential Type，存在性类型。
   *
   * Array【T】
   * Array【_】也就是存在一种类型
   * */
}















