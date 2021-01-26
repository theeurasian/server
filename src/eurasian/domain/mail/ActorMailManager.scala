package eurasian.domain.mail

import akka.actor.Actor
import eurasian.domain.mail.ActorMailManager.Mail
import org.simplejavamail.api.mailer.config.TransportStrategy
import org.simplejavamail.email.EmailBuilder
import org.simplejavamail.mailer.MailerBuilder

object ActorMailManager{
  case class Mail(toUser: String, toMail: String, subject: String, message: String)
}

class ActorMailManager extends Actor{
  override def preStart(): Unit = {
    //self ! Mail("Bogdan Isaev", "redeeming.fury@gmail.com", "subject","Some message")
  }
  override def receive: Receive = {
    case mailInfo:Mail =>
      val mailer = MailerBuilder
        .withSMTPServer("smtp.mail.ru", 465, "noreply@xn--41a.tv", "VideoService123")
        .withSessionTimeout(10 * 1000)
        .withTransportStrategy(TransportStrategy.SMTPS)
        .clearEmailAddressCriteria()
        .buildMailer()

      val mail = EmailBuilder.startingBlank()
          .from("Смотри Я.TV", "noreply@xn--41a.tv")
          .to(mailInfo.toUser, mailInfo.toMail)
          .withSubject("Смотри Я.TV " + mailInfo.subject)
          .withHTMLText(mailInfo.message)
          .buildEmail()

      mailer.sendMail(mail);
    case _ => None
  }
  def mailBody(msg: String): String ={
    val body = "<img src=\"//counter.rambler.ru/top100.cnt?pid=7089123\"/>\n<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">\n<html xmlns=\"http://www.w3.org/1999/xhtml\">\n<head>\n  <meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\" />\n  <title>Nautic Rus DeepSea</title>\n  <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\"/>\n</head>\n<body style=\"margin: 0; padding: 0;\">\n<table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"100%\">\n  <tr>\n    <td style=\"padding: 10px 0 30px 0;\">\n      <table align=\"center\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"600\" style=\"border: 1px solid #cccccc; border-collapse: collapse;\">\n        <tr>\n          <td align=\"center\" bgcolor=\"#325a7c\" style=\"color: #153643; font-size: 28px; font-weight: bold; font-family: Arial, sans-serif;\">\n            <span style=\"color: #ffffff\">Nautic Rus DeepSea</span>\n          </td>\n        </tr>\n        <tr>\n          <td bgcolor=\"#ffffff\" style=\"padding: 40px 30px 40px 30px;\">\n            <table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"100%\">\n              <tr>\n                <td style=\"color: #153643; font-family: Book Antiqua, sans-serif; font-size: 24px; text-align: center\">\n                  <b>&message</b>\n                </td>\n              </tr>\n            </table>\n          </td>\n        </tr>\n        <tr>\n          <td bgcolor=\"#325a7c\" style=\"padding: 30px 30px 30px 30px;\">\n            <table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"100%\">\n              <tr>\n                <td style=\"color: #ffffff; font-family: Book Antiqua, sans-serif; font-size: 14px;\" width=\"75%\">\n                  <a href=\"https://nautic-rus.ru/\" target=\"_blank\" style=\"color: #ffffff; font-size: 24px\"><font color=\"#ffffff\"> nautic-rus.ru</font></a><br>\n                </td>\n                <td align=\"right\" width=\"25%\">\n                  <table border=\"0\" cellpadding=\"0\" cellspacing=\"0\">\n                    <tr>\n                      <td style=\"font-family: Book Antiqua, sans-serif; font-size: 12px; font-weight: bold;\">\n                        <a href=\"https://www.instagram.com/\" style=\"cursor: pointer; color: #ffffff;\" target=\"_blank\">\n                          instagram\n                        </a>\n                      </td>\n                      <td style=\"font-size: 0; line-height: 0;\" width=\"20\">&nbsp;</td>\n                      <td style=\"font-family: Book Antiqua, sans-serif; font-size: 12px; font-weight: bold;\">\n                        <a href=\"https://twitter.com/\" style=\"cursor: pointer; color: #ffffff;\" target=\"_blank\">\n                          twitter\n                        </a>\n                      </td>\n                    </tr>\n                  </table>\n                </td>\n              </tr>\n            </table>\n          </td>\n        </tr>\n      </table>\n    </td>\n  </tr>\n</table>\n</body>\n</html>"
    body.replaceAll("&message", msg)
  }
}
