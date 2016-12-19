package scala.beifengwang

import java.io.FileNotFoundException

/**
 * Created by Administrator on 2016/8/1.
 */
import java.io._
object Match {

  class Person
  case class Teacher(name:String,subject:String) extends Person
  case class Student(name:String,classroom:String) extends Person

  def main(args: Array[String]) {
    judgeGrade("GuoYuan","C") // Just so so
    judgeGrade("GuoYuan","D") //GuoYuan,you are a good boy,come on
    judgeGrade("xx","C")
    judgeGrade("xx","F")

    processException(new IllegalArgumentException("expect two arguments,but found one argument"))
    processException(new FileNotFoundException("test,txt not fount"))
    processException(new IOException("get data from socket fail"))
    processException(new ArrayIndexOutOfBoundsException("array is null"))


    greeting(Array("Guo"))
    greeting(Array("lihua","july","jack"))
    greeting(Array("Guo","b","c"))
    greeting(Array("Guo","a","b","c","d"))

    greeting2(List("Guo"))
    greeting2(List("lihua","july","jack"))
    greeting2(List("Guo","b","c"))
    greeting2(List("Guo","a","b","c","d"))


    judgeidentify(Student("GuoYuan","jisuaji"))
    judgeidentify(Teacher("Yao","shujuku"))


    getGrade("a")
    getGrade("c")
    getGrade("d")


  }

  //模式匹配的基础语法
  def judgeGrade(name:String,grade:String): Unit = {
    grade match {
      case "A" => println("Excellent")//对值进行匹配
      case "B" => println("Good")
      case "C" if name == "xx" => println("come on," + name)
      case "C" => println("Just so so")
      case _ if name == "GuoYuan" => println(name + ",you are a good boy,come on") //添加守卫
      case _grade => println("you need to work harder,your grade is " + _grade)//赋值
    }
  }

  //对类型进行模式匹配
  def processException(e:Exception): Unit ={
    e match {
      case e1:IllegalArgumentException => println("you passed illegal argument.exception is : " + e1)
      case e2:FileNotFoundException => println("cannot find the file.exception is: " + e2)
      case e3:IOException => println("io error occurs.exception is " + e3)
      case _:Exception => println("cannot know which exception you hava!")
    }
  }

  //对Array和List的元素进行模式匹配
  def greeting(arr:Array[String]): Unit = {
    arr match {
      case Array("Guo") => println("How are you .Guo")
      case Array(gril1,gril2,gril3) => println("hi,grils,i'm jack ,nice to meet you " + gril1 + ","+gril2+","+gril3)
      case Array("Guo",_*) => println("Hi,Guo,why not introduce your friends to me!")
      case _ => println("who are you")
    }
  }

  def greeting2(arr:List[String]): Unit = {
    arr match {
      case "Guo"::Nil => println("How are you .Guo")
      case gril1::gril2::gril3::Nil => println("hi,grils,i'm jack ,nice to meet you " + gril1 + ","+gril2+","+gril3)
      case "Guo"::tail => println("Hi,Guo,why not introduce your friends to me!")
      case _ => println("who are you")
    }
  }

  /**
   * Scala中提供了一种特殊的类，用case class进行声明，中文也可以称作样例类。case class其实有点类似于java中的javaBean的
   * 概念。即只定义field，并且由Scala编译时自动提供getter和setter方法 ，但是没有method
   * case class的主构造函数接受的参数通常不需要使用var或val修饰，Scala自动就会使用val修饰（但是如果你自己使用var修饰，
   * 那么还是会按照var来）
   * Scala自动为case class定义了伴生对象，也就是object，并且定义了apply()方法，该方法接收主构造函数中相同的参数，并返回
   * case class对象。
   */


  //case class与模式匹配
  def judgeidentify(p:Person): Unit = {
    p match {
      case Teacher(name,subject) => println("Teacher,name is " + name + ",subject you teach is " + subject+".")
      case Student(name,classroom) => println("Student,name is " + name + ",your classroom is " + classroom+".")
      case _ => println("Illegal Access!")
    }
  }

  /**
   * Scala有一种特殊的类型，叫做Option。Option有两种值，一种是Some，表示有值，一种是None，表示没有值
   */
  //Option与模式匹配
  val grades = Map("a" ->"A","b"->"B","c"->"C")
  def getGrade(name:String): Unit = {
    val grade = grades.get(name)
    grade match {
      case Some(grade) => println("your grade is " + grade)
      case None => println("Sorry,your grade information is not in the system")
    }
  }

}



























