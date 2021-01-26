package eurasian.domain.mongo.classes

import eurasian.domain.mongo.MongoManager
import eurasian.domain.video.classes.{Video, VideoComment}

import scala.collection.mutable.ListBuffer

trait MongoVideos extends MongoCollections {
  def addVideo(video: Video): Unit ={
    MongoManager.insert("videos", video.toJson)
  }
  def getVideos: ListBuffer[Video] ={
    val videos: ListBuffer[Video] = ListBuffer.empty[Video]
    val docs = toListBuffer(MongoManager.getDataBase.getCollection("videos"))
    docs.foreach(doc => {
      val video = Video.fromDoc(doc)
      if (video.nonEmpty){
        videos += video.get
      }
    })
    videos.filter(_.removed == 0)
  }
  def addVideoComment(comment: VideoComment): Unit ={
    MongoManager.insert("videoComments", comment.toJson)
  }
  def getVideoComments: ListBuffer[VideoComment] ={
    val comments: ListBuffer[VideoComment] = ListBuffer.empty[VideoComment]
    val docs = toListBuffer(MongoManager.getDataBase.getCollection("videoComments"))
    docs.foreach(doc => {
      val comment = VideoComment.fromDoc(doc)
      if (comment.nonEmpty){
        comments += comment.get
      }
    })
    comments.filter(_.removed == 0)
  }
}
