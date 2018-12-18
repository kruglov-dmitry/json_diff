package dkruglov.json_diff

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.http.scaladsl.Http
import com.typesafe.config.ConfigFactory
import com.typesafe.scalalogging.LazyLogging
import scala.util.Try

object WebServer extends App with LazyLogging {

  def startApplication() = {

    val cfg = ConfigFactory.load()

    implicit val actorSystem: ActorSystem = ActorSystem("Authentication", cfg.getConfig("akka-http-actor-test"))

    // NOTE:  This dispatcher configured with limited amount of threads for pure http serving
    //        But actors have deployment config within app.conf that point out to another dispatcher,
    //        configured to handle many blocking threads
    implicit val blockingDispatcher = actorSystem.dispatchers.lookup("my-thread-pool-dispatcher")

    implicit val materializer: ActorMaterializer  = ActorMaterializer()

    val host = Try(cfg.getString("host")).getOrElse("localhost")
    val port = Try(cfg.getInt("port")).getOrElse(8080)

    val restService = new RestService()

    logger.info(s"Listening at $host:$port")

    Http().bindAndHandle(restService.route, host, port)
  }

  startApplication()

}
