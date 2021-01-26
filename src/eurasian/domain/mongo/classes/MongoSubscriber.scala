package eurasian.domain.mongo.classes

import eurasian.domain.mongo.MongoManager
import eurasian.domain.subscribe.Subscriber
import eurasian.domain.websocket.classes.ClientCredentials

import scala.collection.mutable.ListBuffer

trait MongoSubscriber extends MongoCollections {
  def getSubscribers: ListBuffer[Subscriber] ={
    val subscribers: ListBuffer[Subscriber] = ListBuffer.empty[Subscriber]
    val docs = toListBuffer(MongoManager.getDataBase.getCollection("subscribers"))
    docs.foreach(doc => {
      val subscriber = Subscriber.fromDoc(doc)
      if (subscriber.nonEmpty){
        subscribers += subscriber.get
      }
    })
    subscribers
  }
  def addSubscriber(subscriber: Subscriber): Unit ={
    MongoManager.insert("subscribers", subscriber.toJson)
  }
}
