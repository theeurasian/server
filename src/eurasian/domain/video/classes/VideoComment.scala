package eurasian.domain.video.classes

import org.bson.Document
import play.api.libs.json._

class VideoComment(var id: String, val videoId: String, val text: String, val user: String, var date: String) {
  val removed = 0
  def toJson: JsValue ={
    Json.toJson(this)
  }
}

object VideoComment{
  def fromDoc(doc: Document): Option[VideoComment] ={
    Json.parse(doc.toJson).asOpt[VideoComment]
  }
  implicit val reads: Reads[VideoComment] = new Reads[VideoComment] {
    override def reads (json: JsValue): JsResult[VideoComment] = json.asOpt[JsObject] match {
      case Some (x) => JsSuccess (new VideoComment(
        id = (x \ "id").asOpt[String].getOrElse(""),
        videoId = (x \ "videoId").asOpt[String].getOrElse(""),
        text = (x \ "text").asOpt[String].getOrElse(""),
        user = (x \ "user").asOpt[String].getOrElse(""),
        date = (x \ "date").asOpt[String].getOrElse("")
      ))
      case _ => JsSuccess (null)
    }
  }
  implicit val writes: Writes[VideoComment] = new Writes[VideoComment] {
    override def writes(o: VideoComment): JsValue = o match {
      case x: VideoComment => Json.obj(
        "id" -> x.id,
        "videoId" -> x.videoId,
        "text" -> x.text,
        "user" -> x.user,
        "date" -> x.date
      )
      case _ => JsNull
    }
  }
}