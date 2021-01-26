package eurasian.domain.mongo.classes

import eurasian.domain.mongo.MongoManager
import eurasian.domain.subscribe.Purchase

import scala.collection.mutable.ListBuffer

trait MongoPurchase extends MongoCollections {
  def getPurchases: ListBuffer[Purchase] ={
    val purchases: ListBuffer[Purchase] = ListBuffer.empty[Purchase]
    val docs = toListBuffer(MongoManager.getDataBase.getCollection("purchases"))
    docs.foreach(doc => {
      val purchase = Purchase.fromDoc(doc)
      if (purchase.nonEmpty){
        purchases += purchase.get
      }
    })
    purchases
  }
  def addPurchase(purchase: Purchase): Unit ={
    MongoManager.insert("purchases", purchase.toJson)
  }
}
