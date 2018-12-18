package dkruglov.json_diff

import scala.concurrent.duration._
import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success}
import com.typesafe.scalalogging.LazyLogging
import akka.actor.{ActorSystem, Props}
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.pattern.ask
import akka.util.Timeout
import dkruglov.json_diff.actors.Communications.{AddNewData, DiffEntries}
import dkruglov.json_diff.actors.DataHandler
import dkruglov.json_diff.entities.{DataRequestApi, DiffResult}
import dkruglov.json_diff.enums.Side
import dkruglov.json_diff.enums.Side.Side
import dkruglov.json_diff.traits.{CorsSupport, JsonSupport}


class RestService(implicit executionContext: ExecutionContext, actorSystem: ActorSystem) extends LazyLogging with JsonSupport with CorsSupport {

  implicit val timeout = Timeout(3 seconds)

  val dataHandler = actorSystem.actorOf(Props(new DataHandler()), "DataHandler")

  def addNewDataForComparison(queryId: String, side: Side, dataToCompare: String) = {
    dataHandler ? AddNewData(DataRequestApi(queryId, side, dataToCompare))
  }

  def compareDataEntries(queryId: String) = {
    (dataHandler ? DiffEntries(queryId)).mapTo[DiffResult]
  }

  val route: Route =  corsHandler {

    path("healthcheck") {
      get {
        complete(StatusCodes.OK, "Healthcheck")
      }
    } ~
      pathPrefix("v1") {
        pathPrefix("diff") {
          post {
            path(Segment / "right") {
              queryId: String => { entity(as[String]) {
                    dataToCompare => onComplete(addNewDataForComparison(queryId, Side.RIGHT, dataToCompare)) {
                      case Success(_) => complete(StatusCodes.OK, s"Acknowledged - added RIGHT - for queryID $queryId!")
                      case Failure(e) => complete(StatusCodes.BadRequest, e.getMessage)
                    }
                  } }
            } ~
              path(Segment / "left" ) {
                queryId: String => { entity(as[String]) {
                  dataToCompare => onComplete(addNewDataForComparison(queryId, Side.LEFT, dataToCompare)) {
                    case Success(_) => complete(StatusCodes.OK, s"Acknowledged - added LEFT - for queryID $queryId!")
                    case Failure(e) => complete(StatusCodes.BadRequest, e.getMessage)
                  }
                } }
              }
          } ~
          get {
            path(Segment) {
              queryId => onComplete(compareDataEntries(queryId)) {
                case Success(userToken) => complete(StatusCodes.OK, userToken)
                case Failure(e) => complete(StatusCodes.BadRequest, e.getMessage)
              }
            }
          }
      }
    }
  }
}
