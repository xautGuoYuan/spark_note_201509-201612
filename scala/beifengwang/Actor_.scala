package scala.beifengwang

/**
 * Created by Administrator on 2016/8/2.
 */
import scala.actors.Actor
object Actor_ {

  def main(args: Array[String]) {

    val helloActor = new HelloActor
    helloActor.start()
    helloActor ! "GuoYuan"

    val userManagerActor = new UserManagerActor
    userManagerActor.start()
    userManagerActor ! Register("GuoYuan","1234")
    userManagerActor ! Login("GuoYuan","1234")

    val guoTelephone = new GuoTelephone
    guoTelephone.start()
    val jackTelephoneActor = new JackTelephoneActor(guoTelephone)
    jackTelephoneActor.start()
  }

  /**
   * Scala提供了Actor trait来让我们更方便地进行actor多线程编程，就Actor trait就类似于Jav中的Thread和Runnable一样，是
   * 基础的多线程基类和接口。我们只要重写Actor trait的act方法，即可实现自己的线程执行体，与Java中重写run方法类似。此外，
   * 使用start（）方法启动actor；使用！符号，向actor发送消息；actor内部使用receive和模式匹配接受消息
   */

  class HelloActor extends Actor {
    def act(){
      while(true){
        receive{
          case name:String => println("Hello, " + name)
        }
      }
    }
  }

  /**
   * 消息=case class
   * 例如用户注册登录和注册
   */

  case class Login(username:String,password:String)
  case class Register(username:String,password:String)
  class UserManagerActor extends Actor{
    def act(): Unit = {
      while(true) {
        receive{
          case Login(username,password) => println("login: " + username + ": " + password)
          case Register(username,password) => println("register: " + username + ": " + password)
        }
      }
    }
  }

  /**
   * 如果两个Actor之间要互相收发消息，那么scala的建议是，一个actor向另外一个actor发送消息时，同时带上自己的引用；
   * 其他actor收到自己的消息时，直接通过发送消息的actor的引用，即可以给它回复消息
   *
   * 例如：打电话
   */
  case class Message(content:String,sender:Actor)
  class GuoTelephone extends Actor {
    def act(): Unit = {
      while(true) {
        receive{
          case Message(content,sender) => {
            println("Guo telephone " + content)
            sender ! "i'm Guo,please call me after 10 minutes"
          }
        }
      }
    }
  }
  class JackTelephoneActor(val guoTelephoneActor: Actor) extends  Actor {
    def act(): Unit = {
      guoTelephoneActor ! Message("Hello,GuoYuan,i'm jack,are you free now?",this)
      receive{
        case response:String => println("jack telephone: " + response)
      }
    }
  }

  /**
   * 默认情况下，消息都是异步的；但是如果希望发送的消息是同步的，即对方接受后，一定要给自己返回结果，那么可以使用
   *   !?的方式发送消息。即    val reply = actor !? message.
   *
   *   如果要异步发送一个消息，但是在后续要获得消息的返回值，那么可以使用Future，即!!语法。
   *   val future = actor !! message.
   *   val reply = future().
   */

}























