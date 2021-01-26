package eurasian.domain.news

import java.nio.charset.{Charset, CodingErrorAction}

import akka.actor.Actor
import eurasian.domain.actors.ActorManager
import eurasian.domain.news.ActorNewsManager.{CnRss, EnRss, KzRss, RuRss}
import eurasian.domain.news.ActorNewsUpdateManager.UpdateRss
import eurasian.domain.news.classes.RssItem
import org.json.{JSONArray, JSONObject}

import scala.collection.mutable.ListBuffer

object ActorNewsUpdateManager{
  case class UpdateRss()
}
class ActorNewsUpdateManager extends Actor{

  override def preStart(): Unit = {
    val qwe1 = getKzRss
    val qwe2 = getRuRssRT
  }

  override def receive: Receive = {
    case UpdateRss =>
      println("News Update")
      ActorManager.newsManager ! RuRss(getRuRssRT)
      ActorManager.newsManager ! EnRss(getEnRss)
      ActorManager.newsManager ! CnRss(getCnRss)
      ActorManager.newsManager ! KzRss(getKzRss)
    case _ => None
  }

  def getKzRss: ListBuffer[RssItem] ={
    val result = ListBuffer.empty[RssItem]
    try{
      val data = get("https://kaz.zakon.kz/").replaceAll("\\n", "").replaceAll("\\r", "").replaceAll("\\t", "")
      "(?<=<div class='last_feed'>).+(?=<\\/div><a class=all_link)".r.findFirstIn(data) match {
        case Some(aHrefData) =>
          "(?<=<li>)<[^<]+<[^<]+<[^<]+".r.findAllIn(aHrefData).foreach(aHref => {
            "(?<=href=\")[^\"]+".r.findFirstIn(aHref) match {
              case Some(url) =>
                "[^>]+$".r.findFirstIn(aHref) match {
                  case Some(title) =>
                    val sitePost = get("https://www.zakon.kz/" + url.trim).replaceAll("\\n", "").replaceAll("\\r", "").replaceAll("\\t", "")
                    "(?<=ln_time>)[^<]+".r.findFirstIn(aHref) match {
                      case Some(time) =>
                        "(?<=<p class=description>)[^<]+".r.findFirstIn(sitePost) match {
                          case Some(description) =>
                            result += new RssItem(title.trim, "https://www.zakon.kz/" + url.trim, description.trim, "сегодня", time.trim)
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
                "(?<=nbsp;)[^<]+".r.findFirstIn(aHref) match {
                  case Some(time) =>
                    "(?<=<div class=\"article__text__overview\">)[<span>\\s]+[^<]+".r.findFirstIn(sitePost) match {
                      case Some(description) =>
                        result += new RssItem(title.trim, url.trim, description.replaceAll("<span>", "").trim, "сегодня", time.trim)
                      case _ =>
                        "(?<=<div class=\"article__header__anons\">)[^<]+".r.findFirstIn(sitePost) match {
                          case Some(description) =>
                            result += new RssItem(title.trim, url.trim, description.replaceAll("<span>", "").trim, "сегодня", time.trim)
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
  def getCnRss: ListBuffer[RssItem] ={
    val result = ListBuffer.empty[RssItem]
    try{
      val data = get("http://news.cn/").replaceAll("\\n", "").replaceAll("\\r", "").replaceAll("\\t", "")
      "(?<=<ul class=\"dataList01\">).+(?=<\\/ul><h3 class=\"focusWordBlue\")".r.findAllIn(data).foreach(regMatch => {
        "<a href=\"[^\"]+\" target=\"_blank\">[^<]+<\\/a>".r.findAllIn(regMatch).foreach(aHref => {
          "(?<=<a href=\")[^\"]+".r.findFirstIn(aHref) match {
            case Some(url) =>
              if (!url.contains("index") && url.contains("world")){
                "(?<=target=\"_blank\">)[^<]+".r.findFirstIn(aHref) match {
                  case Some(title) =>
                    val sitePost = get(url.trim).replaceAll("\\n", "").replaceAll("\\r", "").replaceAll("\\t", "")
                    "(?<=<span class=\"h-time\">)[^<]+".r.findFirstIn(sitePost) match {
                      case Some(date) =>
                        "(?<=<meta name=\"description\" content=\")[^\"]+".r.findFirstIn(sitePost) match {
                          case Some(description) =>
                            result += new RssItem(title.trim, url.trim, description.trim, date.trim, "")
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
  def getRuRss: ListBuffer[RssItem] ={
    val rssNews = ListBuffer.empty[RssItem]
    val rss = get("https://news.yandex.ru/ru/index.utf8.js").replace("var m_index = ", "")
    val array = new JSONArray(rss)
    array.forEach(item => {
      val jsObj = item.asInstanceOf[JSONObject]
      rssNews += new RssItem(
        jsObj.getString("title"),
        jsObj.getString("url"),
        jsObj.getString("descr"),
        jsObj.getString("date"),
        jsObj.getString("time")
      )
    })
    rssNews
  }
  def get(url: String, connectTimeout: Int = 5000, readTimeout: Int = 5000, requestMethod: String = "GET"): String = {
    import java.net.{URL, HttpURLConnection}
    val connection = (new URL(url)).openConnection.asInstanceOf[HttpURLConnection]
    connection.setConnectTimeout(connectTimeout)
    connection.setReadTimeout(readTimeout)
    connection.setRequestMethod(requestMethod)
    connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/84.0.4147.125 Safari/537.36")
    val inputStream = connection.getInputStream

    val decoder = Charset.forName("UTF-8").newDecoder()
    decoder.onMalformedInput(CodingErrorAction.IGNORE)
    scala.io.Source.fromInputStream(inputStream)(decoder)

    val content = io.Source.fromInputStream(inputStream)(decoder).mkString
    if (inputStream != null) inputStream.close()
    content
  }
}
