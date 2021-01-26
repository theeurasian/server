package eurasian.domain.actors

import akka.actor.{Actor, Props}
import eurasian.domain.mail.ActorMailManager
import eurasian.domain.mongo.MongoManager
import eurasian.domain.news.ActorNewsManager
import eurasian.domain.subscribe.ActorSubscribeManager
import eurasian.domain.video.ActorVideoManager
import eurasian.domain.websocket.ActorSocketManager

class ActorStartUpManager extends Actor{
  override def receive: Receive = {
    case "start" =>
      MongoManager.init()
    case "mongoManagerStarted" =>
      ActorManager.mailManager = ActorManager.system.actorOf(Props[ActorMailManager])
      //ActorManager.videoManager = ActorManager.system.actorOf(Props[ActorVideoManager])
      ActorManager.subscribeManager = ActorManager.system.actorOf(Props[ActorSubscribeManager])
      ActorManager.newsManager = ActorManager.system.actorOf(Props[ActorNewsManager])
      ActorManager.commandHandler = ActorManager.system.actorOf(Props[ActorCommandHandler])
    case "commandHandlerStarted" =>
      ActorManager.socketManager = ActorManager.system.actorOf(Props[ActorSocketManager])
  }
}
