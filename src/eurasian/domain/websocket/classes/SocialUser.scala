package eurasian.domain.websocket.classes

import play.api.libs.json._


class SocialUser(val email: String, val first_name: String, val last_name: String, val network: String) {

}

object SocialUser{
  implicit val reads: Reads[SocialUser] = new Reads[SocialUser] {
    override def reads (json: JsValue): JsResult[SocialUser] = json.asOpt[JsObject] match {
      case Some (x) => JsSuccess (new SocialUser(
        email = (x \ "email").asOpt[String].getOrElse(""),
        first_name = (x \ "first_name").asOpt[String].getOrElse(""),
        last_name = (x \ "last_name").asOpt[String].getOrElse(""),
        network = (x \ "network").asOpt[String].getOrElse("")
      ))
      case _ => JsSuccess (null)
    }
  }
}