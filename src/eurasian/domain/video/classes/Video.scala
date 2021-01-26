package eurasian.domain.video.classes

import org.bson.Document
import play.api.libs.json._

import scala.collection.mutable.ListBuffer

class Video(val id: String, val header: String, val description: String, val length: String, val date: String, val src: String, val thumb: String, val comments: ListBuffer[VideoComment]) {
  val removed = 0
  def toJson: JsValue ={
    Json.toJson(this)
  }
}

object Video{
  def fromDoc(doc: Document): Option[Video] ={
    Json.parse(doc.toJson).asOpt[Video]
  }
  implicit val reads: Reads[Video] = new Reads[Video] {
    override def reads (json: JsValue): JsResult[Video] = json.asOpt[JsObject] match {
      case Some (x) => JsSuccess (new Video(
        id = (x \ "id").asOpt[String].getOrElse(""),
        header = (x \ "header").asOpt[String].getOrElse(""),
        description = (x \ "description").asOpt[String].getOrElse(""),
        length = (x \ "length").asOpt[String].getOrElse(""),
        date = (x \ "date").asOpt[String].getOrElse(""),
        src = (x \ "src").asOpt[String].getOrElse(""),
        thumb = (x \ "thumb").asOpt[String].getOrElse(""),
        comments = (x \ "comments").asOpt[ListBuffer[VideoComment]].getOrElse(ListBuffer.empty[VideoComment]),
      ))
      case _ => JsSuccess (null)
    }
  }
  implicit val writes: Writes[Video] = new Writes[Video] {
    override def writes(o: Video): JsValue = o match {
      case x: Video => Json.obj(
        "id" -> x.id,
        "header" -> x.header,
        "description" -> x.description,
        "length" -> x.length,
        "date" -> x.date,
        "src" -> x.src,
        "thumb" -> x.thumb,
        "comments" -> x.comments
      )
      case _ => JsNull
    }
  }
}