package eurasian.domain.mongo.classes
import scala.collection.JavaConverters._
import com.mongodb.client.MongoCollection
import org.apache.commons.collections4.IteratorUtils
import org.bson.Document

import scala.collection.mutable.ListBuffer

trait MongoCollections {
  def toListBuffer(mongoCollection: MongoCollection[Document]): ListBuffer[Document] ={
    ListBuffer.empty[Document] ++= IteratorUtils.toList(mongoCollection.find().iterator()).asScala
  }
}
