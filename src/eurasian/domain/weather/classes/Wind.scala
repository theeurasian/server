package eurasian.domain.weather.classes

import play.api.libs.json.{JsNull, JsObject, JsResult, JsSuccess, JsValue, Json, Reads, Writes}

object Wind{
  implicit val reads: Reads[Wind] = new Reads[Wind] {
    override def reads (json: JsValue): JsResult[Wind] = json.asOpt[JsObject] match {
      case Some (x) => JsSuccess (new Wind(
        speed = (x \ "speed").asOpt[Double].getOrElse(0),
      ))
      case _ => JsSuccess (null)
    }
  }
  implicit val writes: Writes[Wind] = new Writes[Wind] {
    override def writes(o: Wind): JsValue = o match {
      case x: Wind => Json.obj(
        "speed" -> x.speed,
      )
      case _ => JsNull
    }
  }
}
class Wind(val speed: Double) {

}
