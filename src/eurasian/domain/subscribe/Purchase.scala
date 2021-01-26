package eurasian.domain.subscribe

import java.util.{Calendar, Date}

import org.bson.Document
import play.api.libs.json.{JsNull, JsObject, JsResult, JsSuccess, JsValue, Json, Reads, Writes}


class Purchase(val id: String) {
  val date: Date = Calendar.getInstance().getTime
  def toJson: JsValue ={
    Json.toJson(this)
  }
}
object Purchase{
  def fromDoc(doc: Document): Option[Purchase] ={
    Json.parse(doc.toJson).asOpt[Purchase]
  }
  implicit val reads: Reads[Purchase] = new Reads[Purchase] {
    override def reads (json: JsValue): JsResult[Purchase] = json.asOpt[JsObject] match {
      case Some (x) => JsSuccess (new Purchase(
        id = (x \ "id").asOpt[String].getOrElse("noValue")
      ))
      case _ => JsSuccess (null)
    }
  }
  implicit val writes: Writes[Purchase] = new Writes[Purchase] {
    override def writes(o: Purchase): JsValue = o match {
      case x: Purchase => Json.obj(
        "id" -> x.id,
        "date" -> x.date.toString()
      )
      case _ => JsNull
    }
  }
}
