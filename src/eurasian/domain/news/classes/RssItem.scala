package eurasian.domain.news.classes

import eurasian.domain.websocket.classes.ClientCredentials
import play.api.libs.json.{JsNull, JsValue, Json, Writes}

class RssItem(val title: String, val url: String, val descr: String, val date: String, val time: String) {
}
object RssItem{
  implicit val writes: Writes[RssItem] = new Writes[RssItem] {
    override def writes(o: RssItem): JsValue = o match {
      case x: RssItem => Json.obj(
        "title" -> x.title,
        "url" -> x.url,
        "descr" -> x.descr,
        "date" -> x.date,
        "time" -> x.time
      )
      case _ => JsNull
    }
  }
}