package eurasian.domain.websocket.classes

import org.java_websocket.WebSocket

class WSClient(val webSocket: WebSocket, val credentials: ClientCredentials) extends WSStringify {
  val address: String = "(?<=^\\/)[0-9.]+".r.findFirstIn(webSocket.getRemoteSocketAddress.toString) match {
    case Some(x) => x
    case _ => "noValue"
  }
  def isEqual(wsClient: WSClient): Boolean = {
    wsClient.credentials.login == credentials.login && address == wsClient.address
  }
  override def toString: String = {
    wsToString(webSocket) + ", credentials: " + credentials.toString
  }
}