package eurasian.domain.news

import java.nio.charset.{Charset, CodingErrorAction}
import akka.actor.Actor
import eurasian.domain.actors.ActorManager
import eurasian.domain.news.ActorNewsManager.{ByRss, CnRss, DeRss, EnRss, EsRss, FrRss, GetDeRss, GetEsRss, IdRss, InRss, IrRss, KoRss, KzRss, MnRss, PkRss, QaRss, RuRss, VnRss}
import eurasian.domain.news.ActorNewsUpdateManager.UpdateRss
import eurasian.domain.news.classes.RssItem

import java.text.SimpleDateFormat
import java.util.Date
import scala.collection.mutable.ListBuffer

object ActorNewsUpdateManager{
  case class UpdateRss()
}
class ActorNewsUpdateManager extends Actor{

  override def preStart(): Unit = {
    //ActorManager.newsManager ! CnRss(getMnRss)
    val qwe = getMnRss
    val qw = qwe
//    val qwe1 = getKzRss
//    val qwe2 = getRuRssRT
  }

  override def receive: Receive = {
    case UpdateRss =>
      ActorManager.newsManager ! RuRss(getRuRssRIA)
      ActorManager.newsManager ! EnRss(getEnRss)
      ActorManager.newsManager ! CnRss(getCnRss)
      ActorManager.newsManager ! KzRss(getKzRss)
      ActorManager.newsManager ! EsRss(getEsRssRT)
      ActorManager.newsManager ! DeRss(getDeRssRT)
      ActorManager.newsManager ! PkRss(getPkRss)
      ActorManager.newsManager ! QaRss(getQaRss)
      ActorManager.newsManager ! IrRss(getIrRss)
      ActorManager.newsManager ! InRss(getInRss)
      ActorManager.newsManager ! FrRss(getFrRssRT)
      ActorManager.newsManager ! IdRss(getIdRss)
      ActorManager.newsManager ! KoRss(getKoRss)
      ActorManager.newsManager ! VnRss(getVnRss)
      ActorManager.newsManager ! MnRss(getMnRss)
      ActorManager.newsManager ! ByRss(getByRss)
      println("News Update")
    case _ => None
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
                            result += new RssItem(title.trim, url.trim, description.trim.replaceAll(quotes.toString, ""), time.replaceAll("""+""", ""), time.trim)
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
    val data = get("https://ria.ru/").replaceAll("\\n", "").replaceAll("\\r", "").replaceAll("\\t", "")
    "<span class=\"share\"[^<]+".r.findAllIn(data).foreach(aHref => {
      "(?<=url=\")[^\"]+".r.findFirstIn(aHref) match {
        case Some(url) =>
          "(?<=data-title=\")[^\"]+".r.findFirstIn(aHref) match {
            case Some(title) =>
              val sitePost = get(url.trim).replaceAll("\\n", "").replaceAll("\\r", "").replaceAll("\\t", "")
              "(?<=<div class=\"article__info-date\">)[^>]+>[^<]+".r.findFirstIn(sitePost) match {
                case Some(timeValue) =>
                  val time = "(?<=>).+".r.findFirstIn(timeValue).getOrElse("")
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
  def getCnRss: ListBuffer[RssItem] ={
    val result = ListBuffer.empty[RssItem]
    try{
      val data = get("http://news.cn/").replaceAll("\\n", "").replaceAll("\\r", "").replaceAll("\\t", "")
      "<a href=\"[^\"]+\" target=\"_blank\">[^<]+<\\/a>".r.findAllIn(data).foreach(regMatch => {
        "<a href=\"[^\"]+\" target=\"_blank\">[^<]+<\\/a>".r.findAllIn(regMatch).foreach(aHref => {
          "(?<=<a href=\")[^\"]+".r.findFirstIn(aHref) match {
            case Some(url) =>
              if (!url.contains("index") && (url.contains("world") || url.contains("politic"))){
                "(?<=target=\"_blank\">)[^<]+".r.findFirstIn(aHref) match {
                  case Some(title) =>
                    val sitePost = get(url.trim).replaceAll("\\n", "").replaceAll("\\r", "").replaceAll("\\t", "")
                    "(?<=<span class=\"time\">)[^<]+".r.findFirstIn(sitePost) match {
                      case Some(time) =>
                        "(?<=<span class=\"day\"><em>)[^<]+".r.findFirstIn(sitePost) match {
                          case Some(day) =>
                            "(?<=<meta name=\"description\" content=\")[^\"]+".r.findFirstIn(sitePost) match {
                              case Some(description) =>
                                result += new RssItem(title.trim, url.trim, description.trim, day.trim + " " + time.trim, "")
                              case _ => None
                            }
                          case _ => None
                        }
                      case _ => None
                    }
                  case _ => None
                }
              }
            case _ => None
          }
        })
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
                  "(?<=article-meta__excerpt\"><p>)[^<]+".r.findFirstIn(sitePost) match {
                    case Some(description) =>
                      "(?<=date-time__date\">)[^<]+<[^<]+<[^<]+<".r.findFirstIn(sitePost) match {
                        case Some(dateTime) =>
                          "^[^<]+".r.findFirstIn(dateTime) match {
                            case Some(day) =>
                              "[^>]+$".r.findFirstIn(dateTime) match {
                                case Some(time) =>
                                  result += new RssItem(title.trim, url.trim, description.trim, day.trim + " " + time.trim, "")
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
      case e: Exception => None
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
                      "(?<=description content=\")[^\"]+".r.findFirstIn(sitePost) match {
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
      import java.net.{URL, HttpURLConnection}
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
  def getAsChrome(url: String, connectTimeout: Int = 5000, readTimeout: Int = 5000, requestMethod: String = "GET"): String = {
    try{
      import java.net.{URL, HttpURLConnection}
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
      import java.net.{URL, HttpURLConnection}
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
