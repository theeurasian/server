package eurasian.domain.actors

import akka.actor.Actor
import eurasian.domain.currencies.CurrenciesManager.GetCurrencies
import eurasian.domain.logger.Logger
import eurasian.domain.news.ActorNewsManager.{GetCnRss, GetDeRss, GetEnRss, GetEsRss, GetFrRss, GetIdRss, GetInRss, GetIrRss, GetKoRss, GetKzRss, GetMnRss, GetPkRss, GetQaRss, GetRuRss, GetVnRss}
import eurasian.domain.weather.WeatherManager.{GetWeather, GetWeatherRu}
import eurasian.domain.websocket.ActorSocketManager.{LoginWithPassword, LoginWithSocialToken, LoginWithTicket, RegisterUser, ValidateUser}
import eurasian.domain.websocket.classes.WSCmd

class ActorCommandHandler extends Actor with Logger{
  override def preStart(): Unit = {
    ActorManager.startUpManager ! "commandHandlerStarted"
  }
  override def receive: Receive = {
    case cmd:WSCmd =>
      cmd.name match {
        case "newSubscriber" =>
          ActorManager.subscribeManager ! cmd
        case "commitPurchase" =>
          ActorManager.subscribeManager ! cmd
        case "loginWithTicket" =>
          ActorManager.socketManager ! LoginWithTicket(cmd)
        case "loginWithSocialToken" =>
          ActorManager.socketManager ! LoginWithSocialToken(cmd)
        case "loginWithPassword" =>
          ActorManager.socketManager ! LoginWithPassword(cmd)
        case "registerUser" =>
          ActorManager.socketManager ! RegisterUser(cmd)
        case "validateUser" =>
          ActorManager.socketManager ! ValidateUser(cmd)

        case "getRuRss" =>
          ActorManager.newsManager ! GetRuRss(cmd)
        case "getEnRss" =>
          ActorManager.newsManager ! GetEnRss(cmd)
        case "getCnRss" =>
          ActorManager.newsManager ! GetCnRss(cmd)
        case "getKzRss" =>
          ActorManager.newsManager ! GetKzRss(cmd)
        case "getEsRss" =>
          ActorManager.newsManager ! GetEsRss(cmd)
        case "getDeRss" =>
          ActorManager.newsManager ! GetDeRss(cmd)
        case "getPkRss" =>
          ActorManager.newsManager ! GetPkRss(cmd)
        case "getQaRss" =>
          ActorManager.newsManager ! GetQaRss(cmd)
        case "getInRss" =>
          ActorManager.newsManager ! GetInRss(cmd)
        case "getIrRss" =>
          ActorManager.newsManager ! GetIrRss(cmd)
        case "getFrRss" =>
          ActorManager.newsManager ! GetFrRss(cmd)
        case "getIdRss" =>
          ActorManager.newsManager ! GetIdRss(cmd)
        case "getKoRss" =>
          ActorManager.newsManager ! GetKoRss(cmd)
        case "getVnRss" =>
          ActorManager.newsManager ! GetVnRss(cmd)
        case "getMnRss" =>
          ActorManager.newsManager ! GetMnRss(cmd)
        case "getWeather" =>
          ActorManager.weatherManager ! GetWeather(cmd)
        case "getWeatherRu" =>
          ActorManager.weatherManager ! GetWeatherRu(cmd)
        case "getCurrencies" =>
          ActorManager.currenciesManager ! GetCurrencies(cmd)
        case _ =>
          log.warn("Actor Handler received unknown command from socket " + cmd.getSocketInfo + ". Looks like it's not defined. Command: " + cmd.name)
      }
    case _ => None
  }
}
