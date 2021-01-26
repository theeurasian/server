package eurasian.domain.websocket.classes

import java.util.UUID

import org.bson.Document
import org.joda.time.DateTime
import play.api.libs.json._

import scala.collection.mutable.ListBuffer

case class User(login: String, var password: String, name: String, surname: String, gender: String, birthday: String, email: String, phone: String, var netInfo: String) {
  val id: String = UUID.randomUUID().toString
  val registrationDate: String = DateTime.now().toString()
  var removed = 0
  var ticket: String = "noValue"
  val custom: ListBuffer[KeyValue] = ListBuffer.empty[KeyValue]
  def toJson: JsValue ={
    Json.toJson(this)
  }
}

object User{
  def fromDoc(doc: Document): Option[User] ={
    Json.parse(doc.toJson).asOpt[User]
  }
  implicit val reads: Reads[User] = new Reads[User] {
    override def reads (json: JsValue): JsResult[User] = json.asOpt[JsObject] match {
      case Some (x) => JsSuccess (new User(
        login = (x \ "login").asOpt[String].getOrElse("noValue"),
        password = (x \ "password").asOpt[String].getOrElse("noValue"),
        name = (x \ "name").asOpt[String].getOrElse("noValue"),
        surname = (x \ "surname").asOpt[String].getOrElse("noValue"),
        gender = (x \ "gender").asOpt[String].getOrElse("noValue"),
        birthday = (x \ "birthday").asOpt[String].getOrElse("noValue"),
        email = (x \ "email").asOpt[String].getOrElse("noValue"),
        phone = (x \ "phone").asOpt[String].getOrElse("noValue"),
        netInfo = (x \ "netInfo").asOpt[String].getOrElse("noValue"))
      {
        removed = (x \ "removed").asOpt[Int].getOrElse(0)
      })
      case _ => JsSuccess (null)
    }
  }
  implicit val wirtes: Writes[User] = new Writes[User] {
    override def writes(o: User): JsValue = o match {
      case x: User => Json.obj(
        "login" -> x.login,
        "password" -> x.password,
        "name" -> x.name,
        "surname" -> x.surname,
        "gender" -> x.gender,
        "birthday" -> x.birthday,
        "email" -> x.email,
        "phone" -> x.phone,
        "netInfo" -> x.netInfo,
        "id" -> x.id,
        "netInfo" -> x.netInfo,
        "removed" -> x.removed,
        "custom" -> x.custom,
        "ticket" -> x.ticket
      )
      case _ => JsNull
    }
  }
}