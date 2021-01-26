package eurasian.domain.websocket.classes

import org.bson.Document
import play.api.libs.json._

case class KeyValue(key: String, value: String) {
  def toJson: JsValue ={
    Json.toJson(this)
  }
}

object KeyValue{
  def fromDoc(doc: Document): Option[KeyValue] ={
    Json.parse(doc.toJson).asOpt[KeyValue]
  }
  implicit val reads: Reads[KeyValue] = new Reads[KeyValue] {
    override def reads (json: JsValue): JsResult[KeyValue] = json.asOpt[JsObject] match {
      case Some (x) => JsSuccess (new KeyValue(
        key = (x \ "key").asOpt[String].getOrElse("noValue"),
        value = (x \ "value").asOpt[String].getOrElse("noValue"),
      ))
      case _ => JsSuccess (null)
    }
  }
  implicit val writes: Writes[KeyValue] = new Writes[KeyValue] {
    override def writes(o: KeyValue): JsValue = o match {
      case x: KeyValue => Json.obj(
        "key" -> x.key,
        "value" -> x.value
      )
      case _ => JsNull
    }
  }
}