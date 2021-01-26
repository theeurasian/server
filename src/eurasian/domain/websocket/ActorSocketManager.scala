package eurasian.domain.websocket

import java.util.UUID

import akka.actor.Actor
import eurasian.domain.actors.ActorManager
import eurasian.domain.mail.ActorMailManager.Mail
import eurasian.domain.mongo.MongoManager
import eurasian.domain.websocket.ActorSocketManager.{LoginWithPassword, LoginWithSocialToken, LoginWithTicket, RegisterUser, ValidateUser}
import eurasian.domain.websocket.classes.{ClientCredentials, SocialUser, User, WSClient, WSCmd, WSServer}
import org.apache.commons.lang3.StringEscapeUtils
import org.java_websocket.WebSocket
import play.api.libs.json.{JsLookupResult, Json}

import scala.collection.mutable.ListBuffer

object ActorSocketManager{
  case class LoginWithTicket(cmd: WSCmd)
  case class LoginWithPassword(cmd: WSCmd)
  case class RegisterUser(cmd: WSCmd)
  case class ValidateUser(cmd: WSCmd)
  case class LoginWithSocialToken(cmd: WSCmd)
}

class ActorSocketManager extends Actor{

  private var webSocket: WSServer = _
  private val webClients: ListBuffer[WSClient] = ListBuffer.empty[WSClient]
  private val usersPerValidate: ListBuffer[User] = ListBuffer.empty[User]

  override def preStart(): Unit = {
    webSocket = new WSServer()
    webSocket.start()
    ActorManager.startUpManager ! "socketManagerStarted"
  }
  override def postStop(): Unit = {
    webSocket.stop()
  }

  def addWebClient(cmd: WSCmd, credentials: ClientCredentials, user: User): Unit ={
    credentials.ticket = UUID.randomUUID().toString
    credentials.userId = UUID.randomUUID().toString
    user.password = "hidden"
    user.ticket = credentials.ticket
    webClients += new WSClient(cmd.getSender, credentials)
    MongoManager.addWebClient(credentials)
  }
  def removeWebClient(ws: WebSocket, credentials: ClientCredentials): Unit ={
    webClients.find(_.webSocket == ws) match {
      case Some(client) =>
        webClients -= client
      case _ => None
    }
  }

  override def receive: Receive = {
    case msg: RegisterUser =>
      msg.cmd.body.asInstanceOf[JsLookupResult].asOpt[User] match {
        case Some(user) =>
          user.netInfo = msg.cmd.getSocketInfo
          val users = MongoManager.getRegisteredUsers
          (users ++ usersPerValidate).find(x => x.login == user.login || x.email == user.email) match {
            case Some(value) => msg.cmd.reply("registrationUserAlreadyExist")
            case _ =>
              usersPerValidate += user
              msg.cmd.reply("registrationConfirmed")
              ActorManager.mailManager ! Mail(user.surname + " " + user.name, user.email, "Подтверждение регистрации", "Для подтверждения регистрации пройдите по ссылке http://localhost:4200/#/login?validate=" + user.id)
          }
        case _ => None
      }
    case msg: ValidateUser =>
      msg.cmd.body.asInstanceOf[JsLookupResult].asOpt[String] match {
        case Some(userId) =>
          usersPerValidate.find(_.id == userId) match {
            case Some(value) =>
              MongoManager.getRegisteredUsers.find(_.id == userId) match {
                case Some(user) => None
                case _ =>
                  MongoManager.addUser(value)
                  msg.cmd.reply("registrationValidated")
              }
            case _ =>
             None
          }
        case _ => None
      }
    case msg: LoginWithTicket =>
      MongoManager.getWebClients.find(_.ticket == msg.cmd.ticket) match {
        case Some(clientCredentials) =>
          MongoManager.getRegisteredUsers.find(_.login == clientCredentials.login) match {
            case Some(user) =>
              user.password = "hidden"
              user.ticket = clientCredentials.ticket
              msg.cmd.reply("loginConfirmed", user.toJson)
            case _ => None
          }
        case _ =>
          msg.cmd.reply("loginRejectedWrongTicket")
      }
    case msg: LoginWithSocialToken =>
      msg.cmd.body.asInstanceOf[JsLookupResult].asOpt[String] match {
        case Some(token) =>
          val url = s"http://ulogin.ru/token.php?token=" + token
          val content: String = get(url)
          Json.parse(StringEscapeUtils.unescapeJava(content)).asOpt[SocialUser] match {
            case Some(socialUser) =>
              val user = new User("social", "hidden", socialUser.first_name, socialUser.last_name, "", "", socialUser.email, "", socialUser.network)
              MongoManager.addUser(user)
              val credentials = new ClientCredentials("social", UUID.randomUUID().toString, "hidden", UUID.randomUUID().toString)
              user.password = "hidden"
              user.ticket = credentials.ticket
              webClients += new WSClient(msg.cmd.getSender, credentials)
              MongoManager.addWebClient(credentials)
              msg.cmd.reply("loginConfirmed", user.toJson)
            case _ => None
          }
        case _ => None
      }
    case msg: LoginWithPassword =>
      msg.cmd.body.asInstanceOf[JsLookupResult].asOpt[ClientCredentials] match {
        case Some(client) =>
          val users = MongoManager.getRegisteredUsers
          users.find(_.login == client.login) match {
            case Some(value) =>
              users.find(x => x.login == client.login && x.password == client.password) match {
                case Some(user) =>
                  addWebClient(msg.cmd, client, user)
                  msg.cmd.reply("loginConfirmed", user.toJson)
                case _ => msg.cmd.reply("loginRejectedWrongPassword")
              }
            case _ => msg.cmd.reply("loginRejectedWrongUser")
          }
        case _ => None
        }
    case ("clientClosed", cmd: WSCmd) =>
      cmd.body.asInstanceOf[JsLookupResult].asOpt[ClientCredentials] match {
        case Some(client) =>
         removeWebClient(cmd.getSender, client)
        case _ => None
      }
    case _ => None
  }
  private def get(url: String, connectTimeout: Int = 5000, readTimeout: Int = 5000, requestMethod: String = "GET") = {
    import java.net.{HttpURLConnection, URL}
    val connection = (new URL(url)).openConnection.asInstanceOf[HttpURLConnection]
    connection.setConnectTimeout(connectTimeout)
    connection.setReadTimeout(readTimeout)
    connection.setRequestMethod(requestMethod)
    val inputStream = connection.getInputStream
    val content = io.Source.fromInputStream(inputStream).mkString
    if (inputStream != null) inputStream.close()
    content
  }
}
