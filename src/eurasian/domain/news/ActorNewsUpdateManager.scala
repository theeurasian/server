package eurasian.domain.news

import akka.actor.Actor
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.HttpRequest
import eurasian.domain.actors.ActorManager
import eurasian.domain.news.ActorNewsManager._
import eurasian.domain.news.ActorNewsUpdateManager.UpdateRss
import eurasian.domain.news.classes.RssItem
import org.jsoup.Jsoup

import java.nio.charset.{Charset, CodingErrorAction}
import scala.collection.mutable.ListBuffer
import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.{Failure, Success}

object ActorNewsUpdateManager{
  case class UpdateRss()
}
class ActorNewsUpdateManager extends Actor{

  override def preStart(): Unit = {
    val ru = getRuRssIZNew
    //ActorManager.newsManager ! CnRss(getMnRss)
    //getSite("http://english.news.cn/home.htm")
    //val qwe = getAeRssCGTN
    //val qwe1 = getCnRssCGTN
    //val qw = qwe
    //val qw1 = qwe1
//    val qwe1 = getKzRss
//    val qwe2 = getRuRssRT
  }

  implicit val system = ActorManager.system

  override def receive: Receive = {
    case UpdateRss =>
      ActorManager.newsManager ! AeRss(getAeRssCGTN)
      ActorManager.newsManager ! PtRss(getPtRss)
      ActorManager.newsManager ! RuRss(getRuRssIZNew)
      ActorManager.newsManager ! EnRss(getEnRssCGTN)
      ActorManager.newsManager ! CnRss(getCnRssCGTN)
      ActorManager.newsManager ! KzRss(getKzRss)
      ActorManager.newsManager ! EsRss(getEsRssCGTN)
      ActorManager.newsManager ! DeRss(getDeRssRT)
      ActorManager.newsManager ! PkRss(getPkRss)
      ActorManager.newsManager ! QaRss(getQaRss)
      ActorManager.newsManager ! IrRss(getIrRss)
      ActorManager.newsManager ! InRss(getInRss)
      ActorManager.newsManager ! FrRss(getFrRssCGTN)
      ActorManager.newsManager ! IdRss(getIdRss)
      ActorManager.newsManager ! VnRss(getVnRss)
      ActorManager.newsManager ! MnRss(getMnRss)
      ActorManager.newsManager ! ByRss(getByRss)
      ActorManager.newsManager ! KoRss(getKoRss)
      println("News Update")
    case _ => None
  }

  def getPtRss: ListBuffer[RssItem] ={
    val result = ListBuffer.empty[RssItem]
    try{
      val data = get("https://portuguese.news.cn/index.htm").replaceAll("\\n", "").replaceAll("\\r", "").replaceAll("\\t", "")
      "(?<=<li><h3><a href=\")[^\"]+".r.findAllIn(data).foreach(aHref => {
        val url = if (aHref.contains("https://portuguese.news.cn/")){
          aHref
        }
        else{
          "https://portuguese.news.cn/" + aHref
        }
        val sitePost = get(url.trim).replaceAll("\\n", "").replaceAll("\\r", "").replaceAll("\\t", "")
        "(?<=title\" content=\")[^\"]+".r.findFirstIn(sitePost) match {
          case Some(title) =>
            "(?<=<div class=\"info\">)[^<]+".r.findFirstIn(sitePost) match {
              case Some(time) =>
                "<p>[^<]+".r.findFirstIn(sitePost) match {
                  case Some(description) =>
                    val quotes = '"'
                    result += new RssItem(title.trim, url.trim, description.trim.replaceAll("<p>", "").replaceAll("<", "").replaceAll(quotes.toString, ""), time.trim.replaceAll("丨", ""), time.trim.replaceAll("丨", ""))
                  case _ =>
                }
              case _ =>
            }
          case _ => None
        }
      })
    }
    catch {
      case e: Exception => None
    }
    result
  }
  def getByRss: ListBuffer[RssItem] ={
    val result = ListBuffer.empty[RssItem]
    try{
      val data = get("https://bel.sputnik.by/").replaceAll("\\n", "").replaceAll("\\r", "").replaceAll("\\t", "")
      "(?<=cell-carousel__item-link)[^>]+".r.findAllIn(data).foreach(aHrefData => {
        "(?<=href=\")[^\"]+".r.findFirstIn(aHrefData) match {
          case Some(aHref) =>
            val url = "https://bel.sputnik.by/" + aHref
            val sitePost = get(url.trim).replaceAll("\\n", "").replaceAll("\\r", "").replaceAll("\\t", "")
            "(?<=article__title\">)[^<]+".r.findFirstIn(sitePost) match {
              case Some(title) =>
                "(?<=unixtime)[^<]+".r.findFirstIn(sitePost) match {
                  case Some(timeValue) =>
                    "(?<=>).+".r.findFirstIn(timeValue) match {
                      case Some(time) =>
                        "(?<=article__announce-text\">)[^<]+".r.findFirstIn(sitePost) match {
                          case Some(description) =>
                            val quotes = '"'
                            result += new RssItem(title.trim, url.trim, description.trim.replaceAll(quotes.toString, ""), time, time.trim)
                          case _ =>
                        }
                      case _ =>
                    }
                  case _ => None
                }
              case _ => None
            }
          case _ => None
        }
      })
    }
    catch {
      case e: Exception => None
    }
    result
  }
  def getAeRssRT: ListBuffer[RssItem] ={
    val result = ListBuffer.empty[RssItem]
    try{
      val data = getOnly("https://arabic.rt.com/rss/")
      "<item>[^<]+<[^<]+<[^<]+<[^<]+<[^<]+<[^<]+<[^<]+<[^<]+<[^<]+<[^<]+<[^<]+<[^<]+<[^<]+<[^<]+".r.findAllIn(data).foreach(regMatch => {
        val sitePost = regMatch
        "(?<=<title>)[^<]+".r.findFirstIn(sitePost) match {
          case Some(title) =>
            "(?<=<pubDate>)[^<]+(?=\\s\\+0000)".r.findFirstIn(sitePost) match {
              case Some(time) =>
                "(?<=CDATA\\[)[^\\]]+".r.findFirstIn(sitePost) match {
                  case Some(description) =>
                    "(?<=<link>)[^<]+".r.findFirstIn(sitePost) match {
                      case Some(url) =>
                        result += new RssItem(title.replace("/", "").trim, url.replace("<", "").trim, description.replace("<", "").trim, time.replace("<", "").trim, "")
                      case _ => None
                    }
                  case _ => None
                }
              case _ => None
            }
          case _ => None
        }
      })
    }
    catch {
      case e: Exception => println(e.toString)
    }
    result
  }
  def getKzRss: ListBuffer[RssItem] ={
    val result = ListBuffer.empty[RssItem]
    try{
      val data = get("https://kaz.zakon.kz/").replaceAll("\\n", "").replaceAll("\\r", "").replaceAll("\\t", "")
      "(?<=<a href=\")[^<]+<div class=\"zmainCard".r.findAllIn(data).foreach(aHrefData => {
        "^[^\"]+".r.findFirstIn(aHrefData) match {
          case Some(aHref) =>
          val url = "https://kaz.zakon.kz" + aHref
            val sitePost = get(url.trim).replaceAll("\\n", "").replaceAll("\\r", "").replaceAll("\\t", "")
            "(?<=<h1>)[^<]+".r.findFirstIn(sitePost) match {
              case Some(title) =>
                "(?<=datetime=\")[^>]+".r.findFirstIn(sitePost) match {
                  case Some(time) =>
                    "(?<=<div class=\"description\">)[^<]+".r.findFirstIn(sitePost) match {
                      case Some(description) =>
                        val quotes = '"'
                        result += new RssItem(title.trim, url.trim, description.trim.replaceAll(quotes.toString, ""), "сегодня", time.trim)
                      case _ =>
                    }
                  case _ => None
                }
              case _ => None
            }
          case _ => None
        }
      })
    }
    catch {
      case e: Exception => None
    }
    result
  }
  def getRuRssRT: ListBuffer[RssItem] ={
    val result = ListBuffer.empty[RssItem]
    try{
      val data = get("https://russian.rt.com/").replaceAll("\\n", "").replaceAll("\\r", "").replaceAll("\\t", "")
      "(?<=<ul class=\"listing__rows listing__rows_main-news \">).+(?=<div class=\"listing__button listing__button)".r.findFirstIn(data) match {
        case Some(aHrefData) =>
          "(?<=card__date_main-news  \">)[^<]+<[^<]+<[^<]+<[^<]+".r.findAllIn(aHrefData).foreach(aHref => {
            "(?<=href=\")[^\"]+".r.findFirstIn(aHref) match {
              case Some(url) =>
                "[^>]+$".r.findFirstIn(aHref) match {
                  case Some(title) =>
                    val sitePost = get("https://russian.rt.com/" + url.trim).replaceAll("\\n", "").replaceAll("\\r", "").replaceAll("\\t", "")
                    "^[^<]+".r.findFirstIn(aHref) match {
                      case Some(time) =>
                        "(?<=js-mediator-article\">)[^<]+".r.findFirstIn(sitePost) match {
                          case Some(description) =>
                            result += new RssItem(title.trim, "https://russian.rt.com/" + url.trim, description.trim, "сегодня", time.trim)
                          case _ =>
                        }
                      case _ => None
                    }
                  case _ => None
                }
              case _ => None
            }
          })
        case _ => None
      }
    }
    catch {
      case e: Exception => None
    }
    result
  }
  def getRuRssRBC: ListBuffer[RssItem] ={
    val result = ListBuffer.empty[RssItem]
    val data = get("https://www.rbc.ru/").replaceAll("\\n", "").replaceAll("\\r", "").replaceAll("\\t", "")
    "<a href=\"[^\"]+\"\\s+id=\"id_news[^\"]+\"[^>]+>[^>]+>[^>]+>[^>]+>[^>]+>[^>]+>[^>]+>".r.findAllIn(data).foreach(aHref => {
      "(?<=<a href=\")[^\"]+".r.findFirstIn(aHref) match {
        case Some(url) =>
          "(?<=<span class=\"news-feed__item__title\">)[^<]+".r.findFirstIn(aHref) match {
              case Some(title) =>
                val sitePost = get(url.trim).replaceAll("\\n", "").replaceAll("\\r", "").replaceAll("\\t", "")
                "(?<=<span class=\"article__header__date\")[^>]+>[^>]+".r.findFirstIn(sitePost) match {
                  case Some(timeValue) =>
                    val time = "(?<=>)[^<]+".r.findFirstIn(timeValue).getOrElse("")
                    "(?<=<div class=\"article__text__overview\">)[^>]+>[^>]+>".r.findFirstIn(sitePost) match {
                      case Some(description) =>
                        result += new RssItem(title.trim, url.trim, description.replaceAll("<span>", "").replaceAll("</span>", "").trim, "сегодня", time.trim)
                      case _ =>
                        "(?<=<div class=\"article__header__anons\">)[^>]+>[^>]+>".r.findFirstIn(sitePost) match {
                          case Some(description) =>
                            result += new RssItem(title.trim, url.trim, description.replaceAll("<span>", "").replaceAll("</span>", "").trim, "сегодня", time.trim)
                          case _ =>
                            "(?<=<p>)[^<&]+".r.findFirstIn(sitePost) match {
                              case Some(description) =>
                                result += new RssItem(title.trim, url.trim, description.replaceAll("<span>", "").trim + "...", "сегодня", time.trim)
                              case _ => None
                            }
                        }
                    }
                  case _ => None
                }
              case _ => None
            }
        case _ => None
      }
    })
    result
  }
  def getRuRssRIA: ListBuffer[RssItem] ={
    val result = ListBuffer.empty[RssItem]
    val data = get("https://ria.ru/world/").replaceAll("\\n", "").replaceAll("\\r", "").replaceAll("\\t", "")
    "(?<=<a itemprop=\"url\" href=\")[^\"]+\">\\s<\\/a><meta\\sitemprop=\"name\" content=\"[^\"]+".r.findAllIn(data).foreach(aHref => {
      "^[^\"]+".r.findFirstIn(aHref) match {
        case Some(url) =>
          "(?<=content=\").+".r.findFirstIn(aHref) match {
            case Some(title) =>
              val sitePost = get(url.trim).replaceAll("\\n", "").replaceAll("\\r", "").replaceAll("\\t", "")
              "[\\d\\.\\s:]+(?=<\\/a><span class=\"article__info-date)".r.findFirstIn(sitePost) match {
                case Some(time) =>
                  "(?<=second-title\">)[^<]+".r.findFirstIn(sitePost) match {
                    case Some(description) =>
                      result += new RssItem(title.trim, url.trim, description.replaceAll("<span>", "").replaceAll("</span>", "").trim, time.trim, "")
                    case _ => None
                  }
                case _ => None
              }
            case _ => None
          }
        case _ => None
      }
    })
    result
  }
  def getRuRssIZ: ListBuffer[RssItem] ={
    val result = ListBuffer.empty[RssItem]
    val data = get("https://iz.ru/news").replaceAll("\\n", "").replaceAll("\\r", "").replaceAll("\\t", "")
    "<a href=\"[^\"]+\" class=\"short".r.findAllIn(data).foreach(aHref => {
      "(?<=a href=\")[^\"]+".r.findFirstIn(aHref) match {
        case Some(url) =>
          val sitePost = get("https://iz.ru/" + url.trim).replaceAll("\\n", "").replaceAll("\\r", "").replaceAll("\\t", "")
          "(?<=headline\"><span>)[^<]+".r.findFirstIn(sitePost) match {
            case Some(title) =>
              "(?<=>)[^<]+(?=</time)".r.findFirstIn(sitePost) match {
                case Some(time) =>
                  "(?<=alternativeHeadline\">)[^<]+".r.findFirstIn(sitePost) match {
                    case Some(description) =>
                      result += new RssItem(title.trim, "https://iz.ru/" + url.trim, description.replaceAll("<span>", "").replaceAll("</span>", "").trim, time.trim, "")
                    case _ => None
                  }
                case _ => None
              }
            case _ => None
          }
        case _ => None
      }
    })
    result
  }
  def getRuRssIZNew: ListBuffer[RssItem] ={
    val result = ListBuffer.empty[RssItem]
    val data = get("https://vesti365.ru/novosti-gazeta-izvestija/").replaceAll("\\n", "").replaceAll("\\r", "").replaceAll("\\t", "")
    "<li class=\"feed-item\">[^>]+>[^>]+>".r.findAllIn(data).foreach(aHref => {
      "(?<=href=')[^']+".r.findFirstIn(aHref) match {
        case Some(url) =>
          val sitePost = getOnly(url.trim).replaceAll("\\n", "").replaceAll("\\r", "").replaceAll("\\t", "")
          "(?<=headline\"><span>)[^<]+".r.findFirstIn(sitePost) match {
            case Some(title) =>
              "(?<=>)[^<]+(?=</time)".r.findFirstIn(sitePost) match {
                case Some(time) =>
                  "(?<=alternativeHeadline\">)[^<]+".r.findFirstIn(sitePost) match {
                    case Some(description) =>
                      result += new RssItem(title.trim, url.trim, description.replaceAll("<span>", "").replaceAll("</span>", "").trim, time.trim, "")
                    case _ => None
                  }
                case _ => None
              }
            case _ => None
          }
        case _ => None
      }
    })
    result
  }

  def getCnRssCGTN: ListBuffer[RssItem] ={
    val result = ListBuffer.empty[RssItem]
    try{
      val data = get("http://www.news.cn/world/index.html").replaceAll("\\n", "").replaceAll("\\r", "").replaceAll("\\t", "")
      "(?<=tit\"><div><a href=')[^']+".r.findAllIn(data).foreach(aHref => {
        val url = if (aHref.contains("www.news.cn/")){
          aHref
        }
        else{
          "https://www.news.cn" + aHref
        }
        val sitePost = get(url.trim.replaceAll("https", "http")).replaceAll("\\n", "").replaceAll("\\r", "").replaceAll("\\t", "")
        "(?<=class=\"title\">)[^<]+".r.findFirstIn(sitePost) match {
          case Some(title) =>
            "(?<=time\">)[^<]+".r.findFirstIn(sitePost) match {
              case Some(time) =>
                "<p>[^<]+".r.findFirstIn(sitePost) match {
                  case Some(description) =>
                    val quotes = '"'
                    val t = time.split(" ").filter(x => x != "" && x != "|").mkString(" ")
                    result += new RssItem(title.trim, url.trim, description.replaceAll("<p>", "").replaceAll("<", "").replaceAll(quotes.toString, "").trim, t, t)
                  case _ =>
                }
              case _ =>
            }
          case _ => None
        }
      })
    }
    catch {
      case e: Exception => None
    }
    result
  }
  def getEnRss: ListBuffer[RssItem] ={
    val result = ListBuffer.empty[RssItem]
    try{
      val data = get("https://www.politico.eu/").replaceAll("\\n", "").replaceAll("\\r", "").replaceAll("\\t", "")
      "(?<=card__title\">)<[^<]+".r.findAllIn(data).foreach(aHref => {
        if (result.length < 10){
          "(?<=href=\")[^\"]+".r.findFirstIn(aHref) match {
            case Some(url) =>
              "(?<=\">).+".r.findFirstIn(aHref) match {
                case Some(title) =>
                  val sitePost = get(url.trim).replaceAll("\\n", "").replaceAll("\\r", "").replaceAll("\\t", "")
                  "(?<=hero__excerpt\">)[^<]+".r.findFirstIn(sitePost) match {
                    case Some(description) =>
                      "(?<=date-time__date\">)[^<]+<[^<]+<[^<]+<".r.findFirstIn(sitePost) match {
                        case Some(dateTime) =>
                          "^[^<]+".r.findFirstIn(dateTime) match {
                            case Some(day) =>
                              "[^>]+$".r.findFirstIn(dateTime) match {
                                case Some(time) =>
                                  result += new RssItem(title.trim, url.trim, description.trim, day.trim + " " + time.trim, "")
                                  println(result.length)
                                case _ => None
                              }
                            case _ => None
                          }
                        case _ => None
                      }
                    case _ => None
                  }
                case _ => None
              }
            case _ => None
          }
        }
      })
    }
    catch {
      case e: Exception =>
        println(e.toString)
    }
    result
  }
  def getEnRssReuters: ListBuffer[RssItem] ={
    val result = ListBuffer.empty[RssItem]
    try{
      val data = get("https://edition.cnn.com/world").replaceAll("\\n", "").replaceAll("\\r", "").replaceAll("\\t", "")
      "<a href=\"[^\"]+\" class=\"container__link container__link--type-article container_lead-plus-headlines__link".r.findAllIn(data).foreach(aHref => {
        if (result.length < 10){
          "(?<=href=\")[^\"]+".r.findFirstIn(aHref) match {
            case Some(url) =>
              val sitePost = get("https://edition.cnn.com" + url.trim).replaceAll("\\n", "").replaceAll("\\r", "").replaceAll("\\t", "")
              "(?<=\"metaCaption\" class=\"inline-placeholder\">)[^<]+".r.findFirstIn(sitePost) match {
                case Some(description) =>
                  "(?<=>  Published)[^<]+".r.findFirstIn(sitePost) match {
                    case Some(dateTime) =>
                      "(?<=class=\"headline__text inline-placeholder\" id=\"maincontent\">)[^<]+".r.findFirstIn(sitePost) match {
                        case Some(title) =>
                          result += new RssItem(title.trim, "https://edition.cnn.com" + url.trim, description.trim, dateTime.trim, "")
                        case _ => None
                      }
                    case _ => None
                  }
                case _ => None
              }
            case _ => None
          }
        }
      })
    }
    catch {
      case e: Exception =>
        println(e.toString)
    }
    result
  }
  def getEnRssRT: ListBuffer[RssItem] ={
    val result = ListBuffer.empty[RssItem]
    try{
      val data = get("https://rt.com/").replaceAll("\\n", "").replaceAll("\\r", "").replaceAll("\\t", "")
      "<a class=\"main-promobox__link\"[^<]+<\\/a>".r.findAllIn(data).foreach(aHref => {
        "(?<=href=\")[^\"]+".r.findFirstIn(aHref) match {
          case Some(url) =>
            "(?<=>)[^<]+".r.findFirstIn(aHref) match {
              case Some(title) =>
                val sitePost = get("https://rt.com/" + url.trim).replaceAll("\\n", "").replaceAll("\\r", "").replaceAll("\\t", "")
                "(?<=<span class=\"date date_article-header\">)[^<]+".r.findFirstIn(sitePost) match {
                  case Some(date) =>
                    "(?<=<meta name=\"description\" content=\")[^\"]+".r.findFirstIn(sitePost) match {
                      case Some(description) =>
                        result += new RssItem(title.trim, "https://rt.com/" + url.trim, description.trim, date.trim, "")
                      case _ => None
                    }
                  case _ => None
                }
              case _ => None
            }
          case _ => None
        }
      })
    }
    catch {
      case e: Exception => None
    }
    result
  }
  def getEnRssCGTN: ListBuffer[RssItem] ={
    val result = ListBuffer.empty[RssItem]
    try{
      val data = get("http://english.news.cn/world/index.htm").replaceAll("\\n", "").replaceAll("\\r", "").replaceAll("\\t", "")
      "(?<=bigPic02 clearfix\"><a href=\"..)[^\"]+".r.findAllIn(data).foreach(aHref => {
        val url = if (aHref.contains("https://english.news.cn/")){
          aHref
        }
        else{
          "https://english.news.cn" + aHref
        }
        val sitePost = get(url.trim.replaceAll("https", "http")).replaceAll("\\n", "").replaceAll("\\r", "").replaceAll("\\t", "")
        "(?<=title\" content=\")[^\"]+".r.findFirstIn(sitePost) match {
          case Some(title) =>
            "(?<=time\">)[^<]+".r.findFirstIn(sitePost) match {
              case Some(time) =>
                "<p>[^<]+".r.findFirstIn(sitePost) match {
                  case Some(description) =>
                    val quotes = '"'
                    result += new RssItem(title.trim, url.trim, description.trim.replaceAll("<p>", "").replaceAll("<", "").replaceAll(quotes.toString, ""), time.trim.replaceAll("丨", "").trim, time.trim.replaceAll("丨", ""))
                  case _ =>
                }
              case _ =>
            }
          case _ => None
        }
      })
    }
    catch {
      case e: Exception => None
    }
    result
  }
  def getFrRssCGTN: ListBuffer[RssItem] ={
    val result = ListBuffer.empty[RssItem]
    try{
      val data = get("http://french.news.cn/monde/index.htm").replaceAll("\\n", "").replaceAll("\\r", "").replaceAll("\\t", "")
      "(?<=tit\"><a href=\"..)[^\"]+".r.findAllIn(data).foreach(aHref => {
        val url = if (aHref.contains("https://french.news.cn/")){
          aHref
        }
        else{
          "https://french.news.cn" + aHref
        }
        val sitePost = get(url.trim.replaceAll("https", "http")).replaceAll("\\n", "").replaceAll("\\r", "").replaceAll("\\t", "")
        "(?<=title\" content=\")[^\"]+".r.findFirstIn(sitePost) match {
          case Some(title) =>
            "(?<=.news.cn<\\/a>)[^<]+".r.findFirstIn(sitePost) match {
              case Some(time) =>
                "<p>[^<]+".r.findFirstIn(sitePost) match {
                  case Some(description) =>
                    val quotes = '"'
                    val t = time.split(" ").filter(x => x != "" && x != "|").mkString(" ")
                    result += new RssItem(title.trim, url.trim, description.trim.replaceAll("<p>", "").replaceAll("<", "").replaceAll(quotes.toString, "").trim, t, t)
                  case _ =>
                }
              case _ =>
            }
          case _ => None
        }
      })
    }
    catch {
      case e: Exception => None
    }
    result
  }
  def getEsRssCGTN: ListBuffer[RssItem] ={
    val result = ListBuffer.empty[RssItem]
    try{
      val data = get("http://spanish.news.cn/mundo/index.htm").replaceAll("\\n", "").replaceAll("\\r", "").replaceAll("\\t", "")
      "(?<=list-item\"><a href=\"..)[^\"]+".r.findAllIn(data).foreach(aHref => {
        val url = if (aHref.contains("https://spanish.news.cn/")){
          aHref
        }
        else{
          "https://spanish.news.cn" + aHref
        }
        val sitePost = get(url.trim.replaceAll("https", "http")).replaceAll("\\n", "").replaceAll("\\r", "").replaceAll("\\t", "")
        "(?<=title\" content=\")[^\"]+".r.findFirstIn(sitePost) match {
          case Some(title) =>
            "(?<=time\">)[^<]+".r.findFirstIn(sitePost) match {
              case Some(time) =>
                "<p>[^<]+".r.findFirstIn(sitePost) match {
                  case Some(description) =>
                    val quotes = '"'
                    val t = time.split(" ").filter(x => x != "" && x != "|").mkString(" ")
                    result += new RssItem(title.trim, url.trim, description.replaceAll("<p>", "").replaceAll("<", "").replaceAll(quotes.toString, "").trim, t, t)
                  case _ =>
                }
              case _ =>
            }
          case _ => None
        }
      })
    }
    catch {
      case e: Exception => None
    }
    result
  }
  def getAeRssCGTN: ListBuffer[RssItem] ={
    val result = ListBuffer.empty[RssItem]
    try{
      val data = get("http://arabic.news.cn/world/index.htm").replaceAll("\\n", "").replaceAll("\\r", "").replaceAll("\\t", "")
      "(?<=list-item\"><a href=\"..)[^\"]+".r.findAllIn(data).foreach(aHref => {
        val url = if (aHref.contains("https://arabic.news.cn/")){
          aHref
        }
        else{
          "https://arabic.news.cn" + aHref
        }
        val sitePost = get(url.trim.replaceAll("https", "http")).replaceAll("\\n", "").replaceAll("\\r", "").replaceAll("\\t", "")
        "(?<=title\" content=\")[^\"]+".r.findFirstIn(sitePost) match {
          case Some(title) =>
            "(?<=time\">)[^<]+".r.findFirstIn(sitePost) match {
              case Some(time) =>
                "<p>[^<]+".r.findFirstIn(sitePost) match {
                  case Some(description) =>
                    val quotes = '"'
                    val t = time.split(" ").filter(x => x != "" && x != "|").mkString(" ")
                    result += new RssItem(title.trim, url.trim, description.replaceAll("<p>", "").replaceAll("<", "").replaceAll(quotes.toString, "").trim, t, t)
                  case _ =>
                }
              case _ =>
            }
          case _ => None
        }
      })
    }
    catch {
      case e: Exception => None
    }
    result
  }

  def getQaRss: ListBuffer[RssItem] ={
    val result = ListBuffer.empty[RssItem]
    try{
      val data = get("https://nricafe.com/").replaceAll("\\n", "").replaceAll("\\r", "").replaceAll("\\t", "")
      "(?<=post__title\"><a href=\")[^\"]+".r.findAllIn(data).foreach(aHref => {
        val url = aHref
        val sitePost = get(url).replaceAll("\\n", "").replaceAll("\\r", "").replaceAll("\\t", "").replaceAll("&nbsp;", "")
        "(?<=itemprop=\"headline\">)[^<]+".r.findFirstIn(sitePost) match {
          case Some(title) =>
            "(?<=itemprop=\"text\"><p>)[^<]+".r.findFirstIn(sitePost) match {
              case Some(description) =>
                "(?<=itemprop=\"datePublished\">)[^<]+".r.findFirstIn(sitePost) match {
                  case Some(date) =>
                    result += new RssItem(title.trim, url.trim, description.trim.replaceAll("<br />", ""), date, "")
                  case _ => None
                }
              case _ => None
            }
          case _ => None
        }
      })
    }
    catch {
      case e: Exception => None
    }
    result
  }
  def getPkRss: ListBuffer[RssItem] ={
    val result = ListBuffer.empty[RssItem]
    try{
      val data = get("https://dailypakistan.com.pk/").replaceAll("\\n", "").replaceAll("\\r", "").replaceAll("\\t", "")
      "(?<=<div class=\\\"tt-news-img\\\"> <a href=\\\")[^\\\"]+".r.findAllIn(data).foreach(url => {
        val sitePost = get(url).replaceAll("\\n", "").replaceAll("\\r", "").replaceAll("\\t", "")
        "(?<=detail-title-area\">)[^\\/]+".r.findFirstIn(sitePost) match {
          case Some(title) =>
            "(?<=<meta name=\"description\" content=\")[^\"]+".r.findFirstIn(sitePost) match {
              case Some(description) =>
                "(?<=post-meta\"> <span>)[^<]+".r.findFirstIn(sitePost) match {
                  case Some(date) =>
                    result += new RssItem(title.trim.replaceAll("<h1>", "").replaceAll("<", ""), url.trim, description.trim.replaceAll("<br />", ""), date, "")
                  case _ => None
                }
              case _ => None
            }
          case _ => None
        }
      })
    }
    catch {
      case e: Exception => None
    }
    result
  }
  def getEsRssRT: ListBuffer[RssItem] ={
    val result = ListBuffer.empty[RssItem]
    try{
      val data = get("https://actualidad.rt.com/").replaceAll("\\n", "").replaceAll("\\r", "").replaceAll("\\t", "")
      "HeaderNews-type_4 \"><a href=\"[^\"]+\"".r.findAllIn(data).foreach(regMatch => {
        "(?<=<a href=\")[^\"]+".r.findFirstIn(regMatch) match {
          case Some(url) =>
            val sitePost = get("https://actualidad.rt.com/" + url.trim).replaceAll("\\n", "").replaceAll("\\r", "").replaceAll("\\t", "")
            "(?<=<h1 class=\"HeadLine-root HeadLine-type_2 \">)[^<]+".r.findFirstIn(sitePost) match {
              case Some(title) =>
                "(?<=>)[^<]+(?=<\\/time>)".r.findFirstIn(sitePost) match {
                  case Some(time) =>
                    "(?<=<div class=\"Text-root Text-type_1 \">)[^<]+".r.findFirstIn(sitePost) match {
                      case Some(description) =>
                        result += new RssItem(title.trim, "https://actualidad.rt.com/" + url.trim, description.trim, time.trim, "")
                      case _ => None
                    }
                  case _ => None
                }

              case _ => None
            }
          case _ => None
        }
      })
    }
    catch {
      case e: Exception => None
    }
    result
  }
  def getDeRssRT: ListBuffer[RssItem] ={
    val result = ListBuffer.empty[RssItem]
    try{
      val data = get("https://de.rt.com/").replaceAll("\\n", "").replaceAll("\\r", "").replaceAll("\\t", "")
      "HeaderNews-type_5 \"><a href=\"[^\"]+\"".r.findAllIn(data).foreach(regMatch => {
        "(?<=<a href=\")[^\"]+".r.findFirstIn(regMatch) match {
          case Some(url) =>
            val sitePost = get("https://de.rt.com" + url.trim).replaceAll("\\n", "").replaceAll("\\r", "").replaceAll("\\t", "")
            "(?<=<h1 class=\"HeadLine-root HeadLine-type_2 \">)[^<]+".r.findFirstIn(sitePost) match {
              case Some(title) =>
                "(?<=>)[^<]+(?=<\\/time>)".r.findFirstIn(sitePost) match {
                  case Some(time) =>
                    "(?<=<div class=\"Text-root Text-type_1 \">)[^<]+".r.findFirstIn(sitePost) match {
                      case Some(description) =>
                        result += new RssItem(title.trim, "https://de.rt.com" + url.trim, description.trim, time.trim, "")
                      case _ => None
                    }
                  case _ => None
                }

              case _ => None
            }
          case _ => None
        }
      })
    }
    catch {
      case e: Exception => None
    }
    result
  }
  def getInRss: ListBuffer[RssItem] ={
    val result = ListBuffer.empty[RssItem]
    try{
      val data = get("https://www.jansatta.com/").replaceAll("\\n", "").replaceAll("\\r", "").replaceAll("\\t", "")
      "(?<=class=\"entry-title\"><a href=\")[^\"]+".r.findAllIn(data).foreach(aHref => {
        if (result.length < 30){
          val url = aHref
          val sitePost = get(url).replaceAll("\\n", "").replaceAll("\\r", "").replaceAll("\\t", "")
          "(?<=<title>)[^<]+".r.findFirstIn(sitePost) match {
            case Some(title) =>
              "(?<=name=\"description\" content=\")[^\\\"]+".r.findFirstIn(sitePost) match {
                case Some(description) =>
                  "[\\w\\s\\d,]+(?=<\\/time>)".r.findFirstIn(sitePost) match {
                    case Some(date) =>
                      result += new RssItem(title.replaceAll("&nbsp;", "").trim, url.trim, description.trim.replaceAll("<br />", ""), date.trim, "")
                    case _ => None
                  }
                case _ => None
              }
            case _ => None
          }
        }
      })
    }
    catch {
      case e: Exception => None
    }
    result
  }
  def getIrRss: ListBuffer[RssItem] ={
    val result = ListBuffer.empty[RssItem]
    try{
      val data = get("https://iran.ru/").replaceAll("\\n", "").replaceAll("\\r", "").replaceAll("\\t", "")
      "(?<=<li><a href=\")[^\"]+".r.findAllIn(data).foreach(aHref => {
        val url = aHref
        val sitePost = get(url).replaceAll("\\n", "").replaceAll("\\r", "").replaceAll("\\t", "")
        "(?<=<title>)[^<]+".r.findFirstIn(sitePost) match {
          case Some(title) =>
            "(?<=description\" content=\")[^\"]+".r.findFirstIn(sitePost) match {
              case Some(description) =>
                "(?<=<span class='h6' style='font-weight:600;'>)[^<]+".r.findFirstIn(sitePost) match {
                  case Some(date) =>
                    result += new RssItem(title.replaceAll("&nbsp;", "").trim, url.trim, description.trim.replaceAll("<br />", ""), date.trim, "")
                  case _ => None
                }
              case _ => None
            }
          case _ => None
        }
      })
    }
    catch {
      case e: Exception => None
    }
    result
  }
  def getFrRssRT: ListBuffer[RssItem] ={
    val result = ListBuffer.empty[RssItem]
    try{
      val data = get("https://francais.rt.com/").replaceAll("\\n", "").replaceAll("\\r", "").replaceAll("\\t", "")
      "(?<=<a class=\"card__underline\" href=\")[^\"]+".r.findAllIn(data).foreach(regMatch => {
        val url = "https://francais.rt.com" + regMatch
        val sitePost = get(url).replaceAll("\\n", "").replaceAll("\\r", "").replaceAll("\\t", "")
        "(?<=\"article__heading\">)[^<]+".r.findFirstIn(sitePost) match {
          case Some(title) =>
            "(?<=>)[^<]+(?=<\\/time>)".r.findFirstIn(sitePost) match {
              case Some(time) =>
                "(?<=<p class=\"text__summary\">)[^<]+".r.findFirstIn(sitePost) match {
                  case Some(description) =>
                    result += new RssItem(title.trim, url.trim, description.trim, time.trim, "")
                  case _ => None
                }
              case _ => None
            }

          case _ => None
        }
      })
    }
    catch {
      case e: Exception => None
    }
    result
  }
  def getIdRss: ListBuffer[RssItem] ={
    val result = ListBuffer.empty[RssItem]
    try{
      val data = get("https://www.antaranews.com/").replaceAll("\\n", "").replaceAll("\\r", "").replaceAll("\\t", "")
      "(?<=<h3 class=\"latest-news\"><a href=\")[^\"]+".r.findAllIn(data).foreach(aHref => {
        if (result.length < 15){
          val url = aHref
          val sitePost = get(url).replaceAll("\\n", "").replaceAll("\\r", "").replaceAll("\\t", "")
          "(?<=post-title\">)[^<]+".r.findFirstIn(sitePost) match {
            case Some(title) =>
              "(?<=>)[^<]+(?=<br)".r.findFirstIn(sitePost) match {
                case Some(description) =>
                  "(?<=fa-clock-o\"><\\/i>)[^<]+".r.findFirstIn(sitePost) match {
                    case Some(date) =>
                      result += new RssItem(title.replaceAll("&nbsp;", "").trim, url.trim, description.trim.replaceAll("<br />", ""), date.trim, "")
                    case _ => None
                  }
                case _ => None
              }
            case _ => None
          }
        }
      })
    }
    catch {
      case e: Exception => None
    }
    result
  }
  def getKoRss: ListBuffer[RssItem] ={
    val result = ListBuffer.empty[RssItem]
    try{
      val data = get("https://www.hankookilbo.com/").replaceAll("\\n", "").replaceAll("\\r", "").replaceAll("\\t", "")
      "(?<=<a href=\")\\/News[^\"]+".r.findAllIn(data).foreach(aHref => {
        if (result.length < 15){
          val url = "https://www.hankookilbo.com/" + aHref
          val sitePost = get(url).replaceAll("\\n", "").replaceAll("\\r", "").replaceAll("\\t", "")
          "(?<=id=\"articleTitle\" type=\"hidden\" value=\")[^\"]+".r.findFirstIn(sitePost) match {
            case Some(title) =>
              "(?<= id=\"articleContents\" type=\"hidden\" value=\")[^\"]+".r.findFirstIn(sitePost) match {
                case Some(description) =>
                  "(?<=<dd>)[^<]+".r.findFirstIn(sitePost) match {
                    case Some(date) =>
                      result += new RssItem(title.replaceAll("&nbsp;", "").trim, url.trim, description.trim.replaceAll("<br />", ""), date.trim, "")
                    case _ => None
                  }
                case _ => None
              }
            case _ => None
          }
        }
      })
    }
    catch {
      case e: Exception => None
    }
    result
  }
  def getVnRss: ListBuffer[RssItem] ={
    val result = ListBuffer.empty[RssItem]
    try{
      val data = get("https://tuoitre.vn/").replaceAll("\\n", "").replaceAll("\\r", "").replaceAll("\\t", "")
      "(?<=<h2 class=\"title-name\">                    <a href=\")[^\"]+".r.findAllIn(data).foreach(aHref => {
        if (result.length < 15){
          val url = "https://tuoitre.vn" + aHref
          val sitePost = get(url).replaceAll("\\n", "").replaceAll("\\r", "").replaceAll("\\t", "")
          "(?<=<h1 class=\"article-title\">)[^<]+".r.findFirstIn(sitePost) match {
            case Some(title) =>
              "(?<=class=\"sapo\">)[^<]+".r.findFirstIn(sitePost) match {
                case Some(description) =>
                  "(?<=\"date-time\">)[^<]+".r.findFirstIn(sitePost) match {
                    case Some(date) =>
                      result += new RssItem(title.replaceAll("&nbsp;", "").trim, url.trim, description.trim.replaceAll("<br />", ""), date.trim, "")
                    case _ => None
                  }
                case _ => None
              }
            case _ => None
          }
        }
      })
    }
    catch {
      case e: Exception => None
    }
    result
  }
  def getMnRss: ListBuffer[RssItem] ={
    val result = ListBuffer.empty[RssItem]
    try{
      val data = get("https://news.mn/").replaceAll("\\n", "").replaceAll("\\r", "").replaceAll("\\t", "")
      "(?<=<h1 class=\"entry-title \">)[^>]+>[^>]+[^>]+".r.findAllIn(data).foreach(dataContent => {
        if (result.length < 15) {
          "(?<=ahref=)[^>]+".r.findFirstIn(dataContent) match {
            case Some(href) =>
              val url = href.trim
              val sitePost = get(url).replaceAll("\\n", "").replaceAll("\\r", "").replaceAll("\\t", "")
              "(?<=<iclass=\"far fa-clock\"><\\/i>)[^<]+".r.findFirstIn(sitePost) match {
                case Some(date) =>
                  "(?<=<title>)[^|]+".r.findFirstIn(sitePost) match {
                    case Some(title) =>
                      "(?<=description\" content=\")[^\"]+".r.findFirstIn(sitePost) match {
                        case Some(description) =>
                          result += new RssItem(title.replaceAll("&nbsp;", "").trim, url.trim, description.trim.replaceAll("<br />", ""), date.trim, "")
                        case _ => None
                      }
                    case _ => None
                  }
                case _ => None
              }
            case _ => None
          }
        }
      })
    }
    catch {
      case e: Exception => None
    }
    result
  }

  def get(url: String, connectTimeout: Int = 5000, readTimeout: Int = 5000, requestMethod: String = "GET"): String = {
    try{
      import java.net.{HttpURLConnection, URL}
      val connection = (new URL(url)).openConnection.asInstanceOf[HttpURLConnection]
      connection.setConnectTimeout(connectTimeout)
      connection.setReadTimeout(readTimeout)
      connection.setRequestMethod(requestMethod)
      connection.setRequestProperty("User-Agent", "Runtime/1.0")
      val inputStream = connection.getInputStream

      val decoder = Charset.forName("UTF-8").newDecoder()
      decoder.onMalformedInput(CodingErrorAction.IGNORE)
      scala.io.Source.fromInputStream(inputStream)(decoder)

      val content = io.Source.fromInputStream(inputStream)(decoder).mkString
      if (inputStream != null) inputStream.close()
      content
    }
    catch {
      case e: Throwable =>
        println(e.toString)
        getAsChrome(url)
    }
  }
  def getSite(url: String): Unit = {
    Http().singleRequest(HttpRequest(uri = url))
      .onComplete {
        case Success(res) =>
         println(res.toString())
        case Failure(e) =>
          println(e.toString)
      }
  }
  def getOnly(url: String): String = {
    val connection = Jsoup.connect(url)
    connection.userAgent("Mozilla/5.0")
//    connection.referrer("http://www.google.com")
//    connection.ignoreHttpErrors(true)
//    connection.timeout(10000)
    val strHTML = connection.get().html()
    strHTML
  }
  def getAsChrome(url: String, connectTimeout: Int = 5000, readTimeout: Int = 5000, requestMethod: String = "GET"): String = {
    try{
      System.setProperty("http.agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/535.7 (KHTML, like Gecko) Chrome/16.0.912.75 Safari/535.7");
      import java.net.{HttpURLConnection, URL}
      val connection = (new URL(url)).openConnection.asInstanceOf[HttpURLConnection]
      connection.setConnectTimeout(connectTimeout)
      connection.setReadTimeout(readTimeout)
      connection.setRequestMethod(requestMethod)
      connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/98.0.4758.102 Safari/537.36.")
      val inputStream = connection.getInputStream

      val decoder = Charset.forName("UTF-8").newDecoder()
      decoder.onMalformedInput(CodingErrorAction.IGNORE)
      scala.io.Source.fromInputStream(inputStream)(decoder)

      val content = io.Source.fromInputStream(inputStream)(decoder).mkString
      if (inputStream != null) inputStream.close()
      content
    }
    catch {
      case e: Throwable =>
        println(e.toString)
        getAsPostman(url)
    }
  }
  def getAsPostman(url: String, connectTimeout: Int = 5000, readTimeout: Int = 5000, requestMethod: String = "GET"): String = {
    try{
      import java.net.{HttpURLConnection, URL}
      val connection = (new URL(url)).openConnection.asInstanceOf[HttpURLConnection]
      connection.setConnectTimeout(connectTimeout)
      connection.setReadTimeout(readTimeout)
      connection.setRequestMethod(requestMethod)
      connection.setRequestProperty("User-Agent", "PostmanRuntime/7.28.4")
      val inputStream = connection.getInputStream

      val decoder = Charset.forName("UTF-8").newDecoder()
      decoder.onMalformedInput(CodingErrorAction.IGNORE)
      scala.io.Source.fromInputStream(inputStream)(decoder)

      val content = io.Source.fromInputStream(inputStream)(decoder).mkString
      if (inputStream != null) inputStream.close()
      content
    }
    catch {
      case e: Throwable =>
        println(e.toString)
        ""
    }
  }

}
