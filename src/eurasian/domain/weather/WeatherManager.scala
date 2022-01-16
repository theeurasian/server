package eurasian.domain.weather

import akka.actor.{Actor, ActorRef, ActorSystem, Cancellable, Props}
import akka.http.scaladsl.{Http, HttpExt}
import eurasian.domain.logger.Logger
import eurasian.domain.websocket.classes.WSCmd

import java.util.concurrent.TimeUnit
import scala.collection.mutable.ListBuffer
import scala.concurrent.{Await, ExecutionContextExecutor}
import scala.concurrent.duration.Duration
import akka.http.scaladsl.model._
import akka.util.ByteString
import eurasian.domain.actors.ActorManager
import eurasian.domain.actors.ActorManager.system
import eurasian.domain.news.ActorNewsUpdateManager
import eurasian.domain.news.ActorNewsUpdateManager.UpdateRss
import eurasian.domain.weather.WeatherManager.{GetWeather, GetWeatherRu, UpdateWeather}
import eurasian.domain.weather.classes.WeatherInfo
import play.api.libs.json.Json

import scala.util.{Failure, Success}

object WeatherManager{
  case class GetWeather(cmd: WSCmd)
  case class GetWeatherRu(cmd: WSCmd)
  case class UpdateWeather()
}
class WeatherManager extends Actor{


  implicit val system: ActorSystem = ActorSystem()
  implicit val executionContext: ExecutionContextExecutor = system.dispatcher
  val http: HttpExt = Http(system)

  val weathers = List(
    new WeatherInfo("Moscow"),
    new WeatherInfo("Paris"),
    new WeatherInfo("Delhi"),
    new WeatherInfo("Rome"),
    new WeatherInfo("Nur-Sultan"),
    new WeatherInfo("Pekin"),
    new WeatherInfo("Berlin"),
    new WeatherInfo("Jakarta"),
    new WeatherInfo("Islamabad"),
    new WeatherInfo("Ulaanbaatar"),
    new WeatherInfo("Milan"),
    new WeatherInfo("Tehran"),
    new WeatherInfo("Doha"),
    new WeatherInfo("Madrid"),
    new WeatherInfo("Hanoi"),
    new WeatherInfo("London"),
  )
  val weathersRu = List(
    new WeatherInfo("Moscow"),
    new WeatherInfo("Paris"),
    new WeatherInfo("Delhi"),
    new WeatherInfo("Rome"),
    new WeatherInfo("Nur-Sultan"),
    new WeatherInfo("Pekin"),
    new WeatherInfo("Berlin"),
    new WeatherInfo("Jakarta"),
    new WeatherInfo("Islamabad"),
    new WeatherInfo("Ulaanbaatar"),
    new WeatherInfo("Milan"),
    new WeatherInfo("Tehran"),
    new WeatherInfo("Doha"),
    new WeatherInfo("Madrid"),
    new WeatherInfo("Hanoi"),
    new WeatherInfo("London"),
  )
  val executor: ExecutionContextExecutor = system.dispatcher

  override def preStart(): Unit = {
    ActorManager.system.scheduler.schedule(Duration(0, TimeUnit.SECONDS), Duration(10, TimeUnit.MINUTES), ActorManager.weatherManager, UpdateWeather())
  }
  override def receive: Receive = {
    case GetWeather(cmd) =>
      cmd.reply("weather", Json.toJson(weathers.sortBy(x => x.name)))
    case GetWeatherRu(cmd) =>
      cmd.reply("weather", Json.toJson(weathersRu.sortBy(x => x.name)))
    case UpdateWeather() =>
      weathers.foreach(weather => {
        val response = http.singleRequest(HttpRequest(HttpMethods.GET, s"https://api.openweathermap.org/data/2.5/weather?q=${weather.name}&appid=7b807a8e068fa5c5802c879bbc65b42c&units=metric"))
        response.onComplete({
          case Success(value) =>
            value.entity.dataBytes.runFold(ByteString(""))(_ ++ _).foreach { body =>
              Json.parse(body.utf8String).asOpt[WeatherInfo] match {
                case Some(weatherInfo) =>
                  weather.weather = weatherInfo.weather
                  weather.wind = weatherInfo.wind
                  weather.tempMain = weatherInfo.tempMain
                case _ => None
              }
              println("Received weather: " + weather.name)
            }
          case Failure(exception) => println(exception)
        })
      })
      weathersRu.foreach(weather => {
        val response = http.singleRequest(HttpRequest(HttpMethods.GET, s"https://api.openweathermap.org/data/2.5/weather?q=${weather.name}&appid=7b807a8e068fa5c5802c879bbc65b42c&units=metric&lang=ru"))
        response.onComplete({
          case Success(value) =>
            value.entity.dataBytes.runFold(ByteString(""))(_ ++ _).foreach { body =>
              Json.parse(body.utf8String).asOpt[WeatherInfo] match {
                case Some(weatherInfo) =>
                  weather.weather = weatherInfo.weather
                  weather.wind = weatherInfo.wind
                  weather.tempMain = weatherInfo.tempMain
                case _ => None
              }
              println("Received weather:" + weather.name)
            }
          case Failure(exception) => println(exception)
        })
      })
    case _ => None
  }
}
