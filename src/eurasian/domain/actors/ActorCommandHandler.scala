package eurasian.domain.actors

import akka.actor.Actor
import eurasian.domain.logger.Logger
import eurasian.domain.news.ActorNewsManager.{GetCnRss, GetEnRss, GetKzRss, GetRuRss}
import eurasian.domain.websocket.ActorSocketManager.{LoginWithPassword, LoginWithSocialToken, LoginWithTicket, RegisterUser, ValidateUser}
import eurasian.domain.websocket.classes.WSCmd

class ActorCommandHandler extends Actor with Logger{
  override def preStart(): Unit = {
    ActorManager.startUpManager ! "commandHandlerStarted"
  }
  override def receive: Receive = {
    case cmd:WSCmd =>
      cmd.name match {
        case "newSubscriber" =>
          ActorManager.subscribeManager ! cmd
        case "commitPurchase" =>
          ActorManager.subscribeManager ! cmd
        case "loginWithTicket" =>
          ActorManager.socketManager ! LoginWithTicket(cmd)
        case "loginWithSocialToken" =>
          ActorManager.socketManager ! LoginWithSocialToken(cmd)
        case "loginWithPassword" =>
          ActorManager.socketManager ! LoginWithPassword(cmd)
        case "registerUser" =>
          ActorManager.socketManager ! RegisterUser(cmd)
        case "validateUser" =>
          ActorManager.socketManager ! ValidateUser(cmd)

        case "getRuRss" =>
          ActorManager.newsManager ! GetRuRss(cmd)
        case "getEnRss" =>
          ActorManager.newsManager ! GetEnRss(cmd)
        case "getCnRss" =>
          ActorManager.newsManager ! GetCnRss(cmd)
        case "getKzRss" =>
          ActorManager.newsManager ! GetKzRss(cmd)

        case _ =>
          log.warn("Actor Handler received unknown command from socket " + cmd.getSocketInfo + ". Looks like it's not defined. Command: " + cmd.name)
      }
    case _ => None
  }
}
