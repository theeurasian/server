package eurasian.domain.websocket.classes

import java.net.InetSocketAddress

import eurasian.App
import eurasian.domain.actors.ActorManager
import eurasian.domain.logger.Logger
import org.java_websocket.WebSocket
import org.java_websocket.handshake.ClientHandshake
import org.java_websocket.server.WebSocketServer

class WSServer(inetSocketAddress: InetSocketAddress = new InetSocketAddress(App.conf.getString("websocket.host"), App.conf.getInt("websocket.port"))) extends WebSocketServer(inetSocketAddress) with Logger with WSStringify {

//  val STORETYPE = "JKS"
//  val STOREPASSWORD = "Whatab0utus"
//  val KEYPASSWORD = "Whatab0utus"
//
//  val ks: KeyStore = KeyStore.getInstance(STORETYPE)
//  val kf = new File("rsc/cert.jks")
//  ks.load(new FileInputStream(kf), STOREPASSWORD.toCharArray)
//
//  val kmf: KeyManagerFactory = KeyManagerFactory.getInstance("SunX509")
//  kmf.init(ks, KEYPASSWORD.toCharArray)
//  val tmf: TrustManagerFactory = TrustManagerFactory.getInstance("SunX509")
//  tmf.init(ks)
//
//  val sslContext: SSLContext = SSLContext.getInstance("TLS")
//  sslContext.init(kmf.getKeyManagers, tmf.getTrustManagers, null)
//
//  this.setWebSocketFactory(new DefaultSSLWebSocketServerFactory(sslContext))

  override def onOpen(webSocket: WebSocket, clientHandshake: ClientHandshake): Unit = {
    log.info("Opened connection with an address " + wsToString(webSocket) + ".")
  }

  override def onClose(webSocket: WebSocket, i: Int, s: String, b: Boolean): Unit = {
    ActorManager.socketManager ! ("clientClosed", webSocket)
  }

  override def onMessage(webSocket: WebSocket, s: String): Unit = {
    log.info("Received message " + s + " from " + wsToString(webSocket) + ".")
    ActorManager.commandHandler ! new WSCmd(webSocket, s)
  }

  override def onError(webSocket: WebSocket, e: Exception): Unit = {
    log.error("Web socket error has been occurred. Error: " + e.toString)
  }

  override def onStart(): Unit = {
    log.info("WebSocket has started successfully on the " + App.conf.getString("websocket.host") + " with a port " + App.conf.getInt("websocket.port") + ".", primary = true)
  }
}