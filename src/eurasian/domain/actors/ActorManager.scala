package eurasian.domain.actors

import akka.actor.{ActorRef, ActorSystem, Cancellable, Props}
import eurasian.domain.logger.Logger
import java.util.concurrent.TimeUnit

import scala.collection.mutable.ListBuffer
import scala.concurrent.{Await, ExecutionContextExecutor}
import scala.concurrent.duration.Duration

object ActorManager extends Logger{

  var system: ActorSystem = _

  var startUpManager: ActorRef = _
  var commandHandler: ActorRef = _
  var socketManager: ActorRef = _
  var subscribeManager: ActorRef = _
  var mailManager: ActorRef = _
  var videoManager: ActorRef = _
  var newsManager: ActorRef = _

  implicit var executor: ExecutionContextExecutor = _
  private val schedulers: ListBuffer[Cancellable] = ListBuffer.empty[Cancellable]

  def init(): Unit ={
    system = ActorSystem()
    executor = system.dispatcher

    startUpManager =  system.actorOf(Props[ActorStartUpManager])
    startUpManager ! "start"
  }
  def terminate(): Unit ={
    if (system != null){
      schedulers.foreach(sch => sch.cancel())
      system.terminate()
      Await.ready(system.whenTerminated, Duration(30, TimeUnit.SECONDS))
    }
  }
}
