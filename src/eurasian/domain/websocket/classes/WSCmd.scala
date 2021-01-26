package eurasian.domain.websocket.classes

import eurasian.domain.logger.Logger
import org.java_websocket.WebSocket
import play.api.libs.json._

import scala.collection.mutable.ListBuffer

class WSCmd(_name: String, _body: JsValue, _ticket: String, _guid: String, _webSocket: WebSocket, _jsData: String) extends Logger with WSStringify {
  private val webSocket: WebSocket = _webSocket
  private val jsData: String = _jsData

  var name: String = _name
  var ticket: String = _ticket
  var guid: String = _guid
  var body: Any = _body

  def this(_name: String, _body: JsValue) {
    this(_name, _body, "noTicket", "noGuid", null, "noValue")
  }
  def this(_name: String, _body: JsValue, _ticket: String, _guid: String) {
    this(_name, _body, _ticket, _guid, null, "noValue")
  }
  def this(_webSocket: WebSocket, _jsData: String) {
    this("noValue", Json.toJson("noValue"), "noTicket", "noGuid", _webSocket, _jsData)
    if (_jsData != "noValue"){
      try{
        val jsValue = Json.parse(jsData)
        name = (jsValue \ "name").asOpt[String].orNull
        ticket = (jsValue \ "ticket").asOpt[String].orNull
        guid = (jsValue \ "guid").asOpt[String].orNull
        body = jsValue \ "body"
      }
      catch{
        case _: Any => None
      }
    }
  }
  def this(_webSocket: WebSocket) {
    this("noValue", Json.toJson("noValue"), "noTicket", "noGuid", _webSocket, "noValue")
  }

  private def getCopy(_name: String, guid: String, _body: JsValue): WSCmd ={
    val cmd = new WSCmd(webSocket, jsData)
    cmd.name = _name
    cmd.body = _body
    cmd
  }
  private def sendTo(ws: WebSocket): Unit ={
    if (ws != null && ws.isOpen) {
      try{
        ws.send(Json.stringify(this.toJson))
      }
    }
  }

  def isNull: Boolean = name == null || name == "noValue" || ticket == null || guid == null || (body.isInstanceOf[JsLookupResult] && body.asInstanceOf[JsLookupResult].isEmpty)
  def toJson: JsValue ={
    Json.toJson(this)
  }
  def getSender: WebSocket = webSocket
  def reply(_name: String, _body: JsValue): Unit ={
    val cmd = getCopy(_name, guid, _body)
    cmd.sendTo(webSocket)
  }
  def replyToMany(_name: String, _body: JsValue,  _sockets: ListBuffer[WebSocket]): Unit={
    val cmd = getCopy(_name, guid, _body)
    _sockets.foreach(cmd.sendTo)
  }
  def replyToMany(_sockets: ListBuffer[WebSocket]): Unit={
    val cmd = getCopy(_name, guid, _body)
    _sockets.foreach(cmd.sendTo)
  }
  def reply(_name: String): Unit ={
    val cmd = getCopy(_name, guid, Json.toJson("noValue"))
    cmd.sendTo(webSocket)
  }
  def getSocketInfo: String ={
    wsToString(webSocket)
  }
  def getSocketInfoWithoutPort: String ={
    wsToStringWithoutPort(webSocket)
  }
  implicit private val WSCmdWrites: Writes[WSCmd] = new Writes[WSCmd] {
    override def writes(o: WSCmd): JsValue = o match {
      case x: WSCmd => Json.obj(
        "name" -> x.name,
        "ticket" -> x.ticket,
        "guid" -> x.guid,
        "body" -> x.body.asInstanceOf[JsValue]
      )
      case _ => JsNull
    }
  }
}
