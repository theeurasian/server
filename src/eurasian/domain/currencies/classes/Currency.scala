package eurasian.domain.currencies.classes

import play.api.libs.json._

object Currency {
  implicit val reads: Reads[Currency] = new Reads[Currency] {
    override def reads (json: JsValue): JsResult[Currency] = json.asOpt[JsObject] match {
      case Some (x) => JsSuccess (new Currency(
        source = (x \ "source").asOpt[String].getOrElse("")
      ){
        quotes = (x \ "quotes").asOpt[Map[String, Double]].getOrElse(Map.empty[String, Double])
      })
      case _ => JsSuccess (null)
    }
  }
  implicit val writes: Writes[Currency] = new Writes[Currency] {
    override def writes(o: Currency): JsValue = o match {
      case x: Currency => Json.obj(
        "source" -> x.source,
        "quotes" -> x.quotes,
      )
      case _ => JsNull
    }
  }
}
class Currency(val source: String) {
  var quotes: Map[String, Double] = Map.empty[String, Double]
}
