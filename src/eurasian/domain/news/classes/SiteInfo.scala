package eurasian.domain.news.classes

import play.api.libs.json.{JsNull, JsValue, Json, Writes}

class SiteInfo(val url: String, val favIcon: String, val title: String) {

}
object SiteInfo{
  def empty: SiteInfo ={
    new SiteInfo("", "", "")
  }
  implicit val writes: Writes[SiteInfo] = new Writes[SiteInfo] {
    override def writes(o: SiteInfo): JsValue = o match {
      case x: SiteInfo => Json.obj(
        "url" -> x.url,
        "favIcon" -> x.favIcon,
        "title" -> x.title
      )
      case _ => JsNull
    }
  }
}