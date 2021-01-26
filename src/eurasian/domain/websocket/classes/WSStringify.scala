package eurasian.domain.websocket.classes

import org.java_websocket.WebSocket

trait WSStringify {
  def wsToString(ws: WebSocket): String ={
    if (ws != null && ws.getRemoteSocketAddress != null) {
      "(?<=^\\/)[0-9.]+".r.findFirstIn(ws.getRemoteSocketAddress.toString) match {
        case Some(x) => x
        case _ => ws.getRemoteSocketAddress.toString
      }
    }
    else{
      "WebSocket Terminated"
    }
  }
  def wsToStringWithoutPort(ws: WebSocket): String ={
    wsToString(ws).replaceAll(":\\d+", "")
  }
}
