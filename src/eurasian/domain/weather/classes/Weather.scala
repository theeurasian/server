package eurasian.domain.weather.classes

import play.api.libs.json.{JsNull, JsObject, JsResult, JsSuccess, JsValue, Json, Reads, Writes}

object Weather{
  implicit val reads: Reads[Weather] = new Reads[Weather] {
    override def reads (json: JsValue): JsResult[Weather] = json.asOpt[JsObject] match {
      case Some (x) => JsSuccess (new Weather(
        main = (x \ "main").asOpt[String].getOrElse(""),
        description = (x \ "description").asOpt[String].getOrElse(""),
        icon = (x \ "icon").asOpt[String].getOrElse(""),
      ))
      case _ => JsSuccess (null)
    }
  }
  implicit val writes: Writes[Weather] = new Writes[Weather] {
    override def writes(o: Weather): JsValue = o match {
      case x: Weather => Json.obj(
        "main" -> x.main,
        "description" -> x.description,
        "icon" -> x.icon,
      )
      case _ => JsNull
    }
  }
}
class Weather(val main: String, val description: String, val icon: String) {

}
