package eurasian.domain.mongo.classes

import eurasian.domain.mongo.MongoManager
import eurasian.domain.websocket.classes.ClientCredentials

import scala.collection.mutable.ListBuffer

trait MongoWebSocket extends MongoCollections {
  def getWebClients: ListBuffer[ClientCredentials] ={
    val clients: ListBuffer[ClientCredentials] = ListBuffer.empty[ClientCredentials]
    val docs = toListBuffer(MongoManager.getDataBase.getCollection("webclients"))
    docs.foreach(doc => {
      val client = ClientCredentials.fromDoc(doc)
      if (client.nonEmpty){
        clients += client.get
      }
    })
    clients
  }
  def addWebClient(clientCredentials: ClientCredentials): Unit ={
    MongoManager.insert("webclients", clientCredentials.toJson)
  }
}
