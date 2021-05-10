package _002_myakka

import akka.actor.{Actor, ActorSystem, Props};

class PropsExample extends Actor {
  def receive= {
    case msg:String => println(msg+" "+self.path.name)
  }
}


object HelloAkka{
  def main(args:Array[String]){
    var actorSystem = ActorSystem("ActorSystem");
    var actor = actorSystem.actorOf(Props[PropsExample],"PropExample");
    actor ! "Hello from"
  }
}