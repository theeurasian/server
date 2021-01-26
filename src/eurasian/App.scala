package eurasian

import com.typesafe.config.{Config, ConfigFactory}
import eurasian.domain.actors.ActorManager
import eurasian.domain.logger.Logger

import scala.io.StdIn.readLine

object App extends Logger{
  val conf: Config = ConfigFactory.load("app")
  def main(args: Array[String]): Unit = {
    init()
    waitForExit()
    terminate()
    log.info("The process has reached target shutdown.", primary = true)
  }
  def waitForExit(): Unit ={
    var input = readLine().replace("exit", "quit")
    while (input != "quit"){
      try{
        input = readLine()
      }
      catch{
        case _: Any =>
          log.info("Breaking down the system.", primary = true)
      }
    }
    log.info("Processing shutting down the system.", primary = true)
  }
  def init(): Unit ={
    ActorManager.init()
  }
  def terminate(): Unit ={

  }
}
