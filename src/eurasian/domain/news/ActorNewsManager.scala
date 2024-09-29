package eurasian.domain.news


import akka.actor.{Actor, ActorRef, Props}
import eurasian.domain.actors.ActorManager
import eurasian.domain.actors.ActorManager.system
import eurasian.domain.news.ActorNewsManager._
import eurasian.domain.news.ActorNewsUpdateManager.{UpdateRss, UpdateRuRss}
import eurasian.domain.news.classes.RssItem
import eurasian.domain.websocket.classes.WSCmd
import play.api.libs.json.Json

import java.util.concurrent.TimeUnit
import scala.collection.mutable.ListBuffer
import scala.concurrent.ExecutionContextExecutor
import scala.concurrent.duration.Duration

object ActorNewsManager{

  case class GetRuRss(cmd: WSCmd)
  case class GetEnRss(cmd: WSCmd)
  case class GetCnRss(cmd: WSCmd)
  case class GetKzRss(cmd: WSCmd)
  case class GetEsRss(cmd: WSCmd)
  case class GetDeRss(cmd: WSCmd)
  case class GetPkRss(cmd: WSCmd)
  case class GetQaRss(cmd: WSCmd)
  case class GetInRss(cmd: WSCmd)
  case class GetIrRss(cmd: WSCmd)
  case class GetFrRss(cmd: WSCmd)
  case class GetIdRss(cmd: WSCmd)
  case class GetKoRss(cmd: WSCmd)
  case class GetVnRss(cmd: WSCmd)
  case class GetMnRss(cmd: WSCmd)
  case class GetByRss(cmd: WSCmd)
  case class GetAeRss(cmd: WSCmd)
  case class GetPtRss(cmd: WSCmd)

  case class RuRss(rss: ListBuffer[RssItem])
  case class EnRss(rss: ListBuffer[RssItem])
  case class CnRss(rss: ListBuffer[RssItem])
  case class KzRss(rss: ListBuffer[RssItem])
  case class EsRss(rss: ListBuffer[RssItem])
  case class DeRss(rss: ListBuffer[RssItem])
  case class PkRss(rss: ListBuffer[RssItem])
  case class QaRss(rss: ListBuffer[RssItem])
  case class InRss(rss: ListBuffer[RssItem])
  case class IrRss(rss: ListBuffer[RssItem])
  case class FrRss(rss: ListBuffer[RssItem])
  case class IdRss(rss: ListBuffer[RssItem])
  case class KoRss(rss: ListBuffer[RssItem])
  case class VnRss(rss: ListBuffer[RssItem])
  case class MnRss(rss: ListBuffer[RssItem])
  case class ByRss(rss: ListBuffer[RssItem])
  case class AeRss(rss: ListBuffer[RssItem])
  case class PtRss(rss: ListBuffer[RssItem])

}
class ActorNewsManager extends Actor{

  val ruRss: ListBuffer[RssItem] = ListBuffer.empty[RssItem]
  val enRss: ListBuffer[RssItem] = ListBuffer.empty[RssItem]
  val cnRss: ListBuffer[RssItem] = ListBuffer.empty[RssItem]
  val kzRss: ListBuffer[RssItem] = ListBuffer.empty[RssItem]
  val esRss: ListBuffer[RssItem] = ListBuffer.empty[RssItem]
  val deRss: ListBuffer[RssItem] = ListBuffer.empty[RssItem]
  val pkRss: ListBuffer[RssItem] = ListBuffer.empty[RssItem]
  val qaRss: ListBuffer[RssItem] = ListBuffer.empty[RssItem]
  val inRss: ListBuffer[RssItem] = ListBuffer.empty[RssItem]
  val irRss: ListBuffer[RssItem] = ListBuffer.empty[RssItem]
  val frRss: ListBuffer[RssItem] = ListBuffer.empty[RssItem]
  val idRss: ListBuffer[RssItem] = ListBuffer.empty[RssItem]
  val koRss: ListBuffer[RssItem] = ListBuffer.empty[RssItem]
  val vnRss: ListBuffer[RssItem] = ListBuffer.empty[RssItem]
  val mnRss: ListBuffer[RssItem] = ListBuffer.empty[RssItem]
  val byRss: ListBuffer[RssItem] = ListBuffer.empty[RssItem]
  val aeRss: ListBuffer[RssItem] = ListBuffer.empty[RssItem]
  val ptRss: ListBuffer[RssItem] = ListBuffer.empty[RssItem]

  implicit var executor: ExecutionContextExecutor = _
  var updateManager: ActorRef = _

  override def preStart(): Unit = {
    executor = system.dispatcher
    updateManager = ActorManager.system.actorOf(Props[ActorNewsUpdateManager])
    ActorManager.system.scheduler.schedule(Duration(0, TimeUnit.SECONDS), Duration(1, TimeUnit.HOURS), updateManager, UpdateRss)
    ActorManager.system.scheduler.schedule(Duration(0, TimeUnit.SECONDS), Duration(5, TimeUnit.MINUTES), updateManager, UpdateRuRss)
  }


  override def receive: Receive = {

    case msg: RuRss =>
      ruRss ++= msg.rss.reverse
      if (ruRss.length > 10){
        val upd = ruRss.clone().drop(ruRss.length - 10)
        ruRss.clear()
        ruRss ++= upd
      }
    case msg: EnRss =>
      enRss ++= msg.rss.reverse
      if (enRss.length > 10){
        val upd = enRss.clone().drop(enRss.length - 10)
        enRss.clear()
        enRss ++= upd
      }
    case msg: CnRss =>
      cnRss ++= msg.rss.reverse
      if (cnRss.length > 10){
        val upd = cnRss.clone().drop(cnRss.length - 10)
        cnRss.clear()
        cnRss ++= upd
      }
    case msg: KzRss =>
      kzRss ++= msg.rss.reverse
      if (kzRss.length > 10){
        val upd = kzRss.clone().drop(kzRss.length - 10)
        kzRss.clear()
        kzRss ++= upd
      }
    case msg: EsRss =>
      esRss ++= msg.rss.reverse
      if (esRss.length > 10){
        val upd = esRss.clone().drop(esRss.length - 10)
        esRss.clear()
        esRss ++= upd
      }
    case msg: DeRss =>
      deRss ++= msg.rss.reverse
      if (deRss.length > 10){
        val upd = deRss.clone().drop(deRss.length - 10)
        deRss.clear()
        deRss ++= upd
      }
    case msg: PkRss =>
      pkRss ++= msg.rss.reverse
      if (pkRss.length > 10){
        val upd = pkRss.clone().drop(pkRss.length - 10)
        pkRss.clear()
        pkRss ++= upd
      }
    case msg: QaRss =>
      qaRss ++= msg.rss.reverse
      if (qaRss.length > 10){
        val upd = qaRss.clone().drop(qaRss.length - 10)
        qaRss.clear()
        qaRss ++= upd
      }
    case msg: InRss =>
      inRss ++= msg.rss.reverse
      if (inRss.length > 10){
        val upd = inRss.clone().drop(inRss.length - 10)
        inRss.clear()
        inRss ++= upd
      }
    case msg: IrRss =>
      irRss ++= msg.rss.reverse
      if (irRss.length > 10){
        val upd = irRss.clone().drop(irRss.length - 10)
        irRss.clear()
        irRss ++= upd
      }
    case msg: FrRss =>
      frRss ++= msg.rss.reverse
      if (frRss.length > 10){
        val upd = frRss.clone().drop(frRss.length - 10)
        frRss.clear()
        frRss ++= upd
      }
    case msg: IdRss =>
      idRss ++= msg.rss.reverse
      if (idRss.length > 10){
        val upd = idRss.clone().drop(idRss.length - 10)
        idRss.clear()
        idRss ++= upd
      }
    case msg: KoRss =>
      koRss ++= msg.rss.reverse
      if (koRss.length > 10){
        val upd = koRss.clone().drop(koRss.length - 10)
        koRss.clear()
        koRss ++= upd
      }
    case msg: VnRss =>
      vnRss ++= msg.rss.reverse
      if (vnRss.length > 10){
        val upd = vnRss.clone().drop(vnRss.length - 10)
        vnRss.clear()
        vnRss ++= upd
      }
    case msg: MnRss =>
      mnRss ++= msg.rss.reverse
      if (mnRss.length > 10){
        val upd = mnRss.clone().drop(mnRss.length - 10)
        mnRss.clear()
        mnRss ++= upd
      }
    case msg: ByRss =>
        byRss ++= msg.rss.reverse
        if (byRss.length > 10){
          val upd = byRss.clone().drop(byRss.length - 10)
          byRss.clear()
          byRss ++= upd
        }
    case msg: AeRss =>
      aeRss ++= msg.rss.reverse
      if (aeRss.length > 10){
        val upd = aeRss.clone().drop(aeRss.length - 10)
        aeRss.clear()
        aeRss ++= upd
      }
    case msg: PtRss =>
      ptRss ++= msg.rss.reverse
      if (ptRss.length > 10){
        val upd = ptRss.clone().drop(ptRss.length - 10)
        ptRss.clear()
        ptRss ++= upd
      }
    case msg: GetRuRss =>
      msg.cmd.reply("rssNews", Json.toJson(ruRss.reverse))
    case msg: GetEnRss =>
      msg.cmd.reply("rssNews", Json.toJson(enRss.reverse))
    case msg: GetCnRss =>
      msg.cmd.reply("rssNews", Json.toJson(cnRss.reverse))
    case msg: GetKzRss =>
      msg.cmd.reply("rssNews", Json.toJson(kzRss.reverse))
    case msg: GetEsRss =>
      msg.cmd.reply("rssNews", Json.toJson(esRss.reverse))
    case msg: GetDeRss =>
      msg.cmd.reply("rssNews", Json.toJson(deRss.reverse))
    case msg: GetPkRss =>
      msg.cmd.reply("rssNews", Json.toJson(pkRss.reverse))
    case msg: GetQaRss =>
      msg.cmd.reply("rssNews", Json.toJson(qaRss.reverse))
    case msg: GetInRss =>
      msg.cmd.reply("rssNews", Json.toJson(inRss.reverse))
    case msg: GetIrRss =>
      msg.cmd.reply("rssNews", Json.toJson(irRss.reverse))
    case msg: GetFrRss =>
      msg.cmd.reply("rssNews", Json.toJson(frRss.reverse))
    case msg: GetIdRss =>
      msg.cmd.reply("rssNews", Json.toJson(idRss.reverse))
    case msg: GetKoRss =>
      msg.cmd.reply("rssNews", Json.toJson(koRss.reverse))
    case msg: GetVnRss =>
      msg.cmd.reply("rssNews", Json.toJson(vnRss.reverse))
    case msg: GetMnRss =>
      msg.cmd.reply("rssNews", Json.toJson(mnRss.reverse))
    case msg: GetByRss =>
      msg.cmd.reply("rssNews", Json.toJson(byRss.reverse))
    case msg: GetAeRss =>
      msg.cmd.reply("rssNews", Json.toJson(aeRss.reverse))
    case msg: GetPtRss =>
      msg.cmd.reply("rssNews", Json.toJson(ptRss.reverse))

    case _ => None
  }
}
