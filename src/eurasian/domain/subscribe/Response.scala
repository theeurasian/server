package eurasian.domain.subscribe


import play.api.libs.json.{JsNull, JsObject, JsResult, JsSuccess, JsValue, Json, Reads, Writes}

class Response(val orderId: String, val formUrl: String) {
  def toJson: JsValue ={
    Json.toJson(this)
  }
}
object Response{
  implicit val reads: Reads[Response] = new Reads[Response] {
    override def reads (json: JsValue): JsResult[Response] = json.asOpt[JsObject] match {
      case Some (x) => JsSuccess (new Response(
        orderId = (x \ "orderId").asOpt[String].getOrElse("noValue"),
        formUrl = (x \ "formUrl").asOpt[String].getOrElse("noValue")
      ))
      case _ => JsSuccess (null)
    }
  }
  implicit val writes: Writes[Response] = new Writes[Response] {
    override def writes(o: Response): JsValue = o match {
      case x: Response => Json.obj(
        "orderId" -> x.orderId,
        "formUrl" -> x.formUrl
      )
      case _ => JsNull
    }
  }
}
