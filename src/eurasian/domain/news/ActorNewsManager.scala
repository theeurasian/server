package eurasian.domain.news


import java.util.concurrent.TimeUnit

import akka.actor.{Actor, ActorRef, Props}
import eurasian.domain.actors.ActorManager
import eurasian.domain.actors.ActorManager.{executor, system}
import eurasian.domain.news.ActorNewsManager.{CnRss, EnRss, GetCnRss, GetEnRss, GetKzRss, GetRuRss, KzRss, RuRss}
import eurasian.domain.news.ActorNewsUpdateManager.UpdateRss
import eurasian.domain.news.classes.RssItem
import eurasian.domain.websocket.classes.WSCmd
import play.api.libs.json.Json

import scala.collection.mutable.ListBuffer
import scala.concurrent.ExecutionContextExecutor
import scala.concurrent.duration.Duration

object ActorNewsManager{

  case class GetRuRss(cmd: WSCmd)
  case class GetEnRss(cmd: WSCmd)
  case class GetCnRss(cmd: WSCmd)
  case class GetKzRss(cmd: WSCmd)

  case class RuRss(rss: ListBuffer[RssItem])
  case class EnRss(rss: ListBuffer[RssItem])
  case class CnRss(rss: ListBuffer[RssItem])
  case class KzRss(rss: ListBuffer[RssItem])
}
class ActorNewsManager extends Actor{

  val ruRss: ListBuffer[RssItem] = ListBuffer.empty[RssItem]
  val enRss: ListBuffer[RssItem] = ListBuffer.empty[RssItem]
  val cnRss: ListBuffer[RssItem] = ListBuffer.empty[RssItem]
  val kzRss: ListBuffer[RssItem] = ListBuffer.empty[RssItem]

  implicit var executor: ExecutionContextExecutor = _
  var updateManager: ActorRef = _

  override def preStart(): Unit = {
    executor = system.dispatcher
    updateManager = ActorManager.system.actorOf(Props[ActorNewsUpdateManager])
    ActorManager.system.scheduler.schedule(Duration(0, TimeUnit.SECONDS), Duration(1, TimeUnit.HOURS), updateManager, UpdateRss)
  }


  override def receive: Receive = {

    case msg: RuRss =>
      ruRss ++= msg.rss
      if (ruRss.length > 10){
        val upd = ruRss.clone().drop(ruRss.length - 10)
        ruRss.clear()
        ruRss ++= upd
      }
    case msg: EnRss =>
      enRss ++= msg.rss
      if (enRss.length > 10){
        val upd = enRss.clone().drop(enRss.length - 10)
        enRss.clear()
        enRss ++= upd
      }
    case msg: CnRss =>
      cnRss ++= msg.rss
      if (cnRss.length > 10){
        val upd = cnRss.clone().drop(cnRss.length - 10)
        cnRss.clear()
        cnRss ++= upd
      }
    case msg: KzRss =>
      kzRss ++= msg.rss
      if (kzRss.length > 10){
        val upd = kzRss.clone().drop(kzRss.length - 10)
        kzRss.clear()
        kzRss ++= upd
      }

    case msg: GetRuRss =>
      msg.cmd.reply("rssNews", Json.toJson(ruRss))
    case msg: GetEnRss =>
      msg.cmd.reply("rssNews", Json.toJson(enRss))
    case msg: GetCnRss =>
      msg.cmd.reply("rssNews", Json.toJson(cnRss))
    case msg: GetKzRss =>
      msg.cmd.reply("rssNews", Json.toJson(kzRss))

    case _ => None
  }
}
