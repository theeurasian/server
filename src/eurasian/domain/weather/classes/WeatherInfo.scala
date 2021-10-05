package eurasian.domain.weather.classes

import eurasian.domain.websocket.classes.KeyValue
import play.api.libs.json.{JsNull, JsObject, JsResult, JsSuccess, JsValue, Json, Reads, Writes}

object WeatherInfo{
  implicit val reads: Reads[WeatherInfo] = new Reads[WeatherInfo] {
    override def reads (json: JsValue): JsResult[WeatherInfo] = json.asOpt[JsObject] match {
      case Some (x) => JsSuccess (new WeatherInfo(
        name = (x \ "name").asOpt[String].getOrElse(""),
      ){
        lang = (x \ "lang").asOpt[String].getOrElse("")
        weather = (x \ "weather").asOpt[List[Weather]].getOrElse(List.empty[Weather])
        tempMain = (x \ "main").asOpt[TempMain].getOrElse(new TempMain(0, 0))
        wind = (x \ "wind").asOpt[Wind].getOrElse(new Wind(0))
      })
      case _ => JsSuccess (null)
    }
  }
  implicit val writes: Writes[WeatherInfo] = new Writes[WeatherInfo] {
    override def writes(o: WeatherInfo): JsValue = o match {
      case x: WeatherInfo => Json.obj(
        "name" -> x.name,
        "lang" -> x.lang,
        "weather" -> x.weather,
        "tempMain" -> x.tempMain,
        "wind" -> x.wind
      )
      case _ => JsNull
    }
  }
}
class WeatherInfo(val name: String) {
  var lang: String = ""
  var weather: List[Weather] = List.empty[Weather]
  var tempMain: TempMain = new TempMain(0, 0)
  var wind: Wind = new Wind(0)
}
