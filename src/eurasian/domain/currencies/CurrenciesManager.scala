package eurasian.domain.currencies

import akka.actor.{Actor, ActorSystem}
import akka.http.scaladsl.{Http, HttpExt}
import akka.http.scaladsl.model.{HttpMethods, HttpRequest}
import akka.util.ByteString
import eurasian.domain.actors.ActorManager
import eurasian.domain.currencies.CurrenciesManager.{GetCurrencies, UpdateCurrencies}
import eurasian.domain.currencies.classes.Currency
import eurasian.domain.weather.WeatherManager.{GetWeather, GetWeatherRu, UpdateWeather}
import eurasian.domain.weather.classes.WeatherInfo
import eurasian.domain.websocket.classes.WSCmd
import play.api.libs.json.Json

import java.util.concurrent.TimeUnit
import scala.concurrent.ExecutionContextExecutor
import scala.concurrent.duration.Duration
import scala.util.{Failure, Success}

object CurrenciesManager{
  case class GetCurrencies(cmd: WSCmd)
  case class UpdateCurrencies()
}
class CurrenciesManager extends Actor{

  implicit val system: ActorSystem = ActorSystem()
  implicit val executionContext: ExecutionContextExecutor = system.dispatcher
  val http: HttpExt = Http(system)

  val executor: ExecutionContextExecutor = system.dispatcher
  val currencies = List(
    new Currency("BYN"),
    new Currency("CNY"),
    new Currency("INR"),
    new Currency("KZT"),
    new Currency("EUR"),
    new Currency("USD"),
    new Currency("RUB"))

  override def preStart(): Unit = {
    ActorManager.system.scheduler.schedule(Duration(0, TimeUnit.SECONDS), Duration(20, TimeUnit.HOURS), ActorManager.currenciesManager, UpdateCurrencies())
  }
  override def receive: Receive = {
    case GetCurrencies(cmd) =>
      cmd.reply("currencies", Json.toJson(currencies.sortBy(x => x.source)))
    case UpdateCurrencies() =>
      //val response = http.singleRequest(HttpRequest(HttpMethods.GET, s"http://apilayer.net/api/live?access_key=f666b66beec4bca1c0423168f39d9d2f"))
      val response = http.singleRequest(HttpRequest(HttpMethods.GET, s"https://eurasian.press/assets/currencies.json"))
      response.onComplete({
        case Success(value) =>
          value.entity.dataBytes.runFold(ByteString(""))(_ ++ _).foreach { body =>
           Json.parse(body.utf8String).asOpt[Currency] match {
              case Some(c) =>
                currencies.last.quotes = c.quotes
                currencies.foreach(currency => {
                  currency.quotes = Map.empty[String, Double]
                  if (currency.source != "USD"){
                    val source = c.quotes("USD" + currency.source)
                    c.quotes.foreach(q => {
                      currency.quotes += ((q._1, q._2 / source))
                    })
                  }
                  else{
                    currency.quotes = c.quotes
                  }
                })
              case _ => None
            }
            println("Received currencies")
          }
        case Failure(exception) => println(exception)
      })
    case _ => None
  }
}
