package eurasian.domain.mongo

import com.mongodb.client.{MongoClient, MongoClients, MongoDatabase}
import org.bson.Document
import play.api.libs.json.{JsValue, Json}
import eurasian.App
import eurasian.domain.actors.ActorManager
import eurasian.domain.logger.Logger
import eurasian.domain.mongo.classes.{MongoCollections, MongoPurchase, MongoSubscriber, MongoUsers, MongoVideos, MongoWebSocket}

object MongoManager extends Logger with MongoCollections with MongoWebSocket with MongoSubscriber with MongoPurchase with MongoVideos with MongoUsers{

  private var client: MongoClient = _
  private var logger: MongoDatabase = _
  private var database: MongoDatabase = _

  def init(): Unit = {

    client = MongoClients.create()
    logger = client.getDatabase(s"${App.conf.getString("app.abbrev")}logger")
    database = client.getDatabase(s"${App.conf.getString("app.abbrev")}database")


    ActorManager.startUpManager ! "mongoManagerStarted"
  }
  def insert(collection: String, doc: Document): Unit = {
    database.getCollection(collection).insertOne(doc)
  }
  def insert(collection: String, value: JsValue): Unit ={
    database.getCollection(collection).insertOne(Document.parse(Json.stringify(value)))
  }
  def setLog(message: String): Unit ={
    val collection = this.logger.getCollection("log")
    collection.insertOne(new Document(collection.countDocuments().toString, message))
  }
  def getDataBase: MongoDatabase = database
}
