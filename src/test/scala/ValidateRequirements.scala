import akka.http.scaladsl.model.StatusCodes.{BadRequest, OK}
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.testkit.{RouteTestTimeout, ScalatestRouteTest}
import dkruglov.json_diff.RestService
import dkruglov.json_diff.utils.Utils._
import dkruglov.json_diff.entities.DataRequestApi
import dkruglov.json_diff.traits.{DataStorage, JsonSupport}
import org.scalatest.{BeforeAndAfter, FunSpec, Matchers}

import scala.concurrent.duration._
import akka.testkit._
import dkruglov.json_diff.entities.DiffResult
import dkruglov.json_diff.enums.{ResultType, Side}
import java.util.UUID.randomUUID

import dkruglov.json_diff.enums.Side.Side

import scala.util.Random


trait RandomSeed {
  val rand = new Random(System.currentTimeMillis())
  def generateQueryId(): String = randomUUID().toString
}

class ValidateRequirements extends FunSpec with Matchers with
  BeforeAndAfter with ScalatestRouteTest with JsonSupport with RandomSeed {

  implicit val timeout = RouteTestTimeout(3.seconds dilated)

  val LenaImgFileName = "data_samples/Lenna.png"
  val HackerImgFileName = "data_samples/Glider.png"

  val lenaBinary = loadFile(LenaImgFileName)
  val lenaBase64 = toBase64(lenaBinary)
  val gliderBase64 = toBase64(loadFile(HackerImgFileName))

  val restService = new RestService()

  before {}

  after {}

  describe("Business logic validation") {

    it ("Compare equal base64 string should return Equal as result of comparison") {
      object DataStorageTest extends DataStorage

      val queryId = generateQueryId()

      DataStorageTest.updateEntry(DataRequestApi(queryId, Side.LEFT, lenaBase64))
      DataStorageTest.updateEntry(DataRequestApi(queryId, Side.RIGHT, lenaBase64))

      val diffResult = DataStorageTest.compareEntries(queryId)

      diffResult.resultType shouldBe ResultType.EQUAL
    }

    it ("Compare equal sized base64 strings with tweaked character should return proper offset as result of comparison") {
      object DataStorageTest extends DataStorage

      val queryId = generateQueryId()

      val idx = rand.nextInt(lenaBinary.length)
      val newValue = Random.alphanumeric.filter(_.isLetter).head

      val midifiedLena = lenaBase64.updated(idx, newValue)

      DataStorageTest.updateEntry(DataRequestApi(queryId, Side.LEFT, lenaBase64))
      DataStorageTest.updateEntry(DataRequestApi(queryId, Side.RIGHT, midifiedLena))

      val diffResult = DataStorageTest.compareEntries(queryId)

      diffResult.resultType shouldBe ResultType.NOT_EQUAL_AT_OFFSET
      diffResult.offset shouldBe idx
    }
  }

  describe("Rest API validation") {
    it ("When we submit equal binary files - server should return Equal as result of comparison") {

      val queryId = generateQueryId()

      Post(s"/v1/diff/$queryId/left", lenaBase64) ~> Route.seal(restService.route) ~> check {
        status shouldBe OK
      }

      Post(s"/v1/diff/$queryId/right", lenaBase64) ~> Route.seal(restService.route) ~> check {
        status shouldBe OK
      }

      Get(s"/v1/diff/$queryId") ~> Route.seal(restService.route) ~> check {
        status shouldBe OK
        responseAs[DiffResult].resultType shouldBe ResultType.EQUAL
      }
    }

    it("When we submit different sized binary files - server should return Different size as result of comparison") {
      val queryId = generateQueryId()

      Post(s"/v1/diff/$queryId/left", lenaBase64) ~> Route.seal(restService.route) ~> check {
        status shouldBe OK
      }

      Post(s"/v1/diff/$queryId/right", gliderBase64) ~> Route.seal(restService.route) ~> check {
        status shouldBe OK
      }

      Get(s"/v1/diff/$queryId") ~> Route.seal(restService.route) ~> check {
        status shouldBe OK
        responseAs[DiffResult].resultType shouldBe ResultType.NOT_EQUAL_DIFFERENT_SIZE
      }
    }

    it("When we submit same sized binary files which are different - server should return Different size as result of comparison") {

      val queryId = generateQueryId()

      val idx = rand.nextInt(lenaBinary.length)
      val newValue = rand.nextInt(127).toByte

      val midifiedLena = toBase64(lenaBinary.updated(idx, newValue))

      Post(s"/v1/diff/$queryId/left", lenaBase64) ~> Route.seal(restService.route) ~> check {
        status shouldBe OK
      }

      Post(s"/v1/diff/$queryId/right", midifiedLena) ~> Route.seal(restService.route) ~> check {
        status shouldBe OK
      }

      Get(s"/v1/diff/$queryId") ~> Route.seal(restService.route) ~> check {
        status shouldBe OK
        responseAs[DiffResult].resultType shouldBe ResultType.NOT_EQUAL_AT_OFFSET
      }
    }

    it("When we enquiry diff for missing parts - we should get error responce") {
      val queryId = generateQueryId()

      Get(s"/v1/diff/$queryId") ~> Route.seal(restService.route) ~> check {
        status shouldBe OK
        responseAs[DiffResult].resultType shouldBe ResultType.ERROR
      }
    }
  }

}