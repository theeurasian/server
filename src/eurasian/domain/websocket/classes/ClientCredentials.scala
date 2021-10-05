package eurasian.domain.websocket.classes

import org.bson.Document
import play.api.libs.json._

import java.util.Date

case class ClientCredentials(login: String, var ticket: String, password: String, var userId: String) {
  val date: String = new Date().getTime.toString
  def toJson: JsValue ={
    Json.toJson(this)
  }

  override def toString: String = {
    this.login + " " + this.password + " " + this.ticket
  }
}

object ClientCredentials{
  def fromDoc(doc: Document): Option[ClientCredentials] ={
    Json.parse(doc.toJson).asOpt[ClientCredentials]
  }
  implicit val reads: Reads[ClientCredentials] = new Reads[ClientCredentials] {
    override def reads (json: JsValue): JsResult[ClientCredentials] = json.asOpt[JsObject] match {
      case Some (x) => JsSuccess (new ClientCredentials(
        login = (x \ "login").asOpt[String].getOrElse("noValue"),
        ticket = (x \ "ticket").asOpt[String].getOrElse("noValue"),
        password = (x \ "password").asOpt[String].getOrElse("noValue"),
        userId = (x \ "userId").asOpt[String].getOrElse("noValue")
      ))
      case _ => JsSuccess (null)
    }
  }
  implicit val writes: Writes[ClientCredentials] = new Writes[ClientCredentials] {
    override def writes(o: ClientCredentials): JsValue = o match {
      case x: ClientCredentials => Json.obj(
        "login" -> x.login,
        "ticket" -> x.ticket,
        "date" -> x.date,
        "userId" -> x.userId
      )
      case _ => JsNull
    }
  }
}