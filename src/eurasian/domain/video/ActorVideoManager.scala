package eurasian.domain.video

import java.text.SimpleDateFormat
import java.util.{Calendar, UUID}

import akka.actor.Actor
import eurasian.domain.mongo.MongoManager
import eurasian.domain.video.ActorVideoManager.{AddVideoComment, GetVideos}
import eurasian.domain.video.classes.{Video, VideoComment}
import eurasian.domain.websocket.classes.WSCmd
import play.api.libs.json.{JsLookupResult, Json}

import scala.collection.mutable.ListBuffer

object ActorVideoManager{
  case class GetVideos(cmd: WSCmd)
  case class AddVideoComment(cmd: WSCmd)
}

class ActorVideoManager extends Actor{


  override def receive: Receive = {
    case msg: GetVideos =>
      msg.cmd.reply("videos", Json.toJson(getVideos))
    case msg: AddVideoComment =>
      msg.cmd.body.asInstanceOf[JsLookupResult].asOpt[VideoComment] match {
        case Some(videoComment) =>
//          val time = Calendar.getInstance().getTime
//          val format = new SimpleDateFormat("HH:mm dd-MM-yy")
//          val k = format.format(time)
          videoComment.date = new SimpleDateFormat("HH:mm dd-MM-yy").format(Calendar.getInstance().getTime)
          videoComment.id = UUID.randomUUID().toString
          MongoManager.addVideoComment(videoComment)
          msg.cmd.reply("videos", Json.toJson(getVideos))
        case _ => None
      }
    case _ => None
  }
  private def getVideos: ListBuffer[Video] = {
    val videos = MongoManager.getVideos
    val videoComments = MongoManager.getVideoComments
    videos.foreach(video => {
      video.comments ++= videoComments.filter(_.videoId == video.id).sortBy(_.date).reverse
    })
    videos
  }
}
