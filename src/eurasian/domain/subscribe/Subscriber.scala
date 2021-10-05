package eurasian.domain.subscribe

import org.bson.Document
import play.api.libs.json._

import java.util.{Calendar, Date}
import scala.collection.mutable.ListBuffer

class Subscriber(val name: String, val surname: String, val email: String, val phone: String, val purchases: ListBuffer[String], var purchaseId: String, val units: String) {
  val date: Date = Calendar.getInstance().getTime
  def toJson: JsValue ={
    Json.toJson(this)
  }
}
object Subscriber{
  def fromDoc(doc: Document): Option[Subscriber] ={
    Json.parse(doc.toJson).asOpt[Subscriber]
  }
  implicit val reads: Reads[Subscriber] = new Reads[Subscriber] {
    override def reads (json: JsValue): JsResult[Subscriber] = json.asOpt[JsObject] match {
      case Some (x) => JsSuccess (new Subscriber(
        name = (x \ "name").asOpt[String].getOrElse("noValue"),
        surname = (x \ "surname").asOpt[String].getOrElse("noValue"),
        email = (x \ "email").asOpt[String].getOrElse("noValue"),
        phone = (x \ "phone").asOpt[String].getOrElse("noValue"),
        purchases =  (x \ "purchases").asOpt[ListBuffer[String]].getOrElse(ListBuffer.empty[String]),
        purchaseId = (x \ "purchaseId").asOpt[String].getOrElse("noValue"),
        units = (x \ "units").asOpt[String].getOrElse("ru")
      ))
      case _ => JsSuccess (null)
    }
  }
  implicit val writes: Writes[Subscriber] = new Writes[Subscriber] {
    override def writes(o: Subscriber): JsValue = o match {
      case x: Subscriber => Json.obj(
        "name" -> x.name,
        "surname" -> x.surname,
        "email" -> x.email,
        "phone" -> x.phone,
        "purchases" -> x.purchases,
        "purchaseId" -> x.purchaseId,
        "date" -> x.date.toString(),
        "units" -> x.units
      )
      case _ => JsNull
    }
  }
}
