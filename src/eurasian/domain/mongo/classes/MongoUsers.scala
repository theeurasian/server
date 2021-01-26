package eurasian.domain.mongo.classes

import eurasian.domain.mongo.MongoManager
import eurasian.domain.websocket.classes.User

import scala.collection.mutable.ListBuffer

trait MongoUsers extends MongoCollections {
  def addUser(registration: User): Unit ={
    MongoManager.insert("users", registration.toJson)
  }
  def getRegisteredUsers: ListBuffer[User] ={
    val users: ListBuffer[User] = ListBuffer.empty[User]
    val docs = toListBuffer(MongoManager.getDataBase.getCollection("users"))
    docs.foreach(doc => {
      val user = User.fromDoc(doc)
      if (user.nonEmpty){
        users += user.get
      }
    })
    users.filter(_.removed == 0)
  }
}
