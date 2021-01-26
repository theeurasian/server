package eurasian.domain.logger

import java.io.{File, FileWriter, PrintWriter}
import java.util.Calendar

import eurasian.domain.actors.ActorManager
import eurasian.domain.mongo.MongoManager


trait Logger {
  object log{
    private val logFile = new File("app.log")

    def info(msg: String, primary: Boolean = false, hidden: Boolean = false): Unit ={
      if (primary){
        if (!hidden) println(Console.MAGENTA + "[INFO " + Calendar.getInstance().getTime + "] " + msg + Console.RESET)
        writeToFile("[INFO " + Calendar.getInstance().getTime + "] " + msg)
      }
      else{
        if (!hidden) println("[INFO " + Calendar.getInstance().getTime + "] " + msg)
        writeToFile("[INFO " + Calendar.getInstance().getTime + "] " + msg)
      }
    }
    def warn(msg: String): Unit ={
      println(Console.YELLOW + "[WARN " + Calendar.getInstance().getTime + "] " + msg + Console.RESET)
      writeToFile("[WARN " + Calendar.getInstance().getTime + "] " + msg)
    }
    def error(msg: String): Unit ={
      println(Console.RED + "[ERROR " + Calendar.getInstance().getTime + "] " + msg + Console.RESET)
      writeToFile("[ERROR " + Calendar.getInstance().getTime + "] " + msg)
    }
    private def writeToMongo(value: String): Unit ={
      MongoManager.setLog(value)
    }
    private def writeToFile(value: String): Unit ={
      writeToMongo(value)
      val logWriter = new FileWriter(logFile, true)
      logWriter.write(value + "\n")
      logWriter.close()
    }
  }
}
