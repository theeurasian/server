package eurasian.domain.subscribe

import java.net.URLEncoder
import java.util.UUID

import akka.actor.Actor
import eurasian.domain.logger.Logger
import eurasian.domain.mongo.MongoManager
import eurasian.domain.websocket.classes.WSCmd
import org.simplejavamail.api.mailer.config.TransportStrategy
import org.simplejavamail.email.EmailBuilder
import org.simplejavamail.mailer.MailerBuilder
import play.api.libs.json.{JsLookupResult, Json}

import scala.collection.mutable.ListBuffer

class ActorSubscribeManager extends Actor with Logger{

  val publishes: ListBuffer[Publish] = ListBuffer(
    new Publish("e1", "Выпуск № 1", "aThnDzx6EU"),
    new Publish("e2", "Выпуск № 2", "x422nNCiVm"),
    new Publish("e3", "Выпуск № 3", "LmA0MG5E7I"),
    new Publish("e4", "Выпуск № 4", "mAXhDHP8uC"),
    new Publish("e5", "Выпуск № 5", "uz2f1qMukt"),
    new Publish("e6", "Выпуск № 6", "ChfNNwxmJv"),
    new Publish("e7", "Выпуск № 7", "JZ7Hnjg4aW"),
    new Publish("e8", "Выпуск № 8", "a2gSkZx2JS"),
    new Publish("e9", "Выпуск № 9", "22EwjKKh113"),
    new Publish("e10", "Выпуск № 10", "10_44821561")

  )

  def get(url: String, connectTimeout: Int = 5000, readTimeout: Int = 5000, requestMethod: String = "GET") =
  {
    import java.net.{URL, HttpURLConnection}
    val connection = (new URL(url)).openConnection.asInstanceOf[HttpURLConnection]
    connection.setConnectTimeout(connectTimeout)
    connection.setReadTimeout(readTimeout)
    connection.setRequestMethod(requestMethod)
    val inputStream = connection.getInputStream
    val content = io.Source.fromInputStream(inputStream).mkString
    if (inputStream != null) inputStream.close()
    content
  }

  override def receive: Receive = {
    case cmd: WSCmd =>
      cmd.name match {
        case "newSubscriber" =>
          cmd.body.asInstanceOf[JsLookupResult].asOpt[Subscriber] match {
            case Some(subscriber) =>

              subscriber.purchaseId = UUID.randomUUID().toString.replace("-", "").substring(0, 15)
              val subscribers = MongoManager.getSubscribers
              while (subscribers.exists(_.purchaseId == subscriber.purchaseId)){
                subscriber.purchaseId = UUID.randomUUID().toString.replace("-", "").substring(0, 15)
              }
              var currency = subscriber.units match {
                case "eu" => 840
                case "cn" => 156
                case _ => 643
              }
              currency = 643
              val language = subscriber.units match {
                case "eu" => "en"
                case "cn" => "en"
                case _ => "ru"
              }
              MongoManager.addSubscriber(subscriber)
              val url = s"https://securepayments.sberbank.ru/payment/rest/register.do?userName=eurasian-api&password=ThisIsH0wWeD0&orderNumber=${subscriber.purchaseId}&amount=${subscriber.purchases.length * 10000}&jsonParams=${URLEncoder.encode(subscriber.toJson.toString(), "UTF-8")}&currency=$currency&language=$language&returnUrl=https://eurasian.press/%23/%3Fid%3D${subscriber.purchaseId}%26lang%3D${subscriber.units}"
              //val content = get(url)
              //val k = 0;
              try{
                Json.parse(get(url)).asOpt[Response] match {
                  case Some(response) =>
                    cmd.reply("payment", Json.toJson(response.formUrl))
                  case _ => None
                }
              }
              catch  {
                case ex: Throwable => println(ex.toString)
              }

            case _ => None
          }
        case "commitPurchase" =>
          cmd.body.asInstanceOf[JsLookupResult].asOpt[String] match {
            case Some(purchaseId) =>
              MongoManager.addPurchase(new Purchase(purchaseId))
              MongoManager.getSubscribers.find(_.purchaseId == purchaseId) match {
                case Some(subscriber) =>
//                  val mailer = MailerBuilder
//                    .withSMTPServer("mail.hosting.reg.ru", 587, "subscribe@eurasian.press", "G3n2T3j2")
//                    .withSessionTimeout(10 * 1000)
//                    .withTransportStrategy(TransportStrategy.SMTP)
//                    .clearEmailAddressCriteria()
//                    .buildMailer()
                  val mailer = MailerBuilder
                    .withSMTPServer("smtp.yandex.ru", 587, "subscribe@eurasian.press", "C2mmqPpq")
                    .withSessionTimeout(10 * 1000)
                    .withTransportStrategy(TransportStrategy.SMTP)
                    .clearEmailAddressCriteria()
                    .buildMailer()
                  subscriber.purchases.foreach(purchase => {
                    publishes.find(_.id == purchase) match {
                      case Some(publish) =>
                        log.info("Publish " + publish.name + " has been sent to " + subscriber.surname + ' ' + subscriber.name + '.')
                        val mail = EmailBuilder.startingBlank()
                          .from("The Eurasian Journal", "subscribe@eurasian.press")
                          .to(subscriber.name + " " + subscriber.surname, subscriber.email)
                          .withSubject("The Eurasian Journal")
                          .withHTMLText(publish.getHtml(subscriber.units))
                          .buildEmail()
                        mailer.sendMail(mail);
                      case _ => None
                    }
                  })
                case _ => None
              }
            case _ => None
          }
      }
    case _ => None
  }
}
