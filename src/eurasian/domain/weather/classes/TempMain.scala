package eurasian.domain.weather.classes

import play.api.libs.json.{JsNull, JsObject, JsResult, JsSuccess, JsValue, Json, Reads, Writes}

object TempMain{
  implicit val reads: Reads[TempMain] = new Reads[TempMain] {
    override def reads (json: JsValue): JsResult[TempMain] = json.asOpt[JsObject] match {
      case Some (x) => JsSuccess (new TempMain(
        temp = (x \ "temp").asOpt[Double].getOrElse(0),
        feels_like = (x \ "feels_like").asOpt[Double].getOrElse(0),
      ))
      case _ => JsSuccess (null)
    }
  }
  implicit val writes: Writes[TempMain] = new Writes[TempMain] {
    override def writes(o: TempMain): JsValue = o match {
      case x: TempMain => Json.obj(
        "temp" -> x.temp,
        "feels_like" -> x.feels_like,
      )
      case _ => JsNull
    }
  }
}
class TempMain(val temp: Double, val feels_like: Double) {

}
