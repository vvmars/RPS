package servises

import actors.RestActor
import routers.ThrottlingRouters
import akka.actor.ActorRef
import design.LocalCache
//import akka.http.scaladsl.marshalling.Marshal
import akka.http.scaladsl.model.{ContentTypes, HttpRequest, MessageEntity, StatusCodes}
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.testkit.ScalatestRouteTest
import design.SystemInfo
import org.scalatest.concurrent.ScalaFutures
import org.scalatest._
import servises.accessory.ThrottlingServiceImpStub

class RestServiceTest
  extends FeatureSpec
    with Matchers
    with ScalaFutures
    with ScalatestRouteTest
    with GivenWhenThen
    with ThrottlingRouters{

  import ThrottlingServiceImpStub._

  override val restActor: ActorRef =
    system.actorOf(RestActor.props, "restActor")

  lazy val routes: Route = throttlingRoutes
  //**************************************************
  def fixture =
    new {
      val currSystemInfo = getSystemInfo
    }
  //**************************************************
  feature("Check REST endpoint") {
    scenario("Perform 'blank' request (GET /sysinfo)") {

      Given("'blank' request and current statistics")
      val f = fixture
      val request = HttpRequest(uri = "/sysinfo")

      When("the request is sent")

      request ~> routes ~> check {

        Then("Status should be OK")
        status should ===(StatusCodes.OK)

        // we expect the response to be json:
        contentType should ===(ContentTypes.`application/json`)

        // and no entries should be in the list:
        entityAs[SystemInfo] should === (f.currSystemInfo)
      }
    }
    //------------------------------------------------
    scenario("Perform request with token (GET /token)") {

      Given("User request with token and current statistics")
      /*
      val userEntity = Marshal("userToken").to[MessageEntity].futureValue
      using the RequestBuilding DSL:
      val request = Get("/sysinfo").withEntity(userEntity)
      */
      val f = fixture
      val request = Get("/sysinfo/good1")
      val currValue: Int = f.currSystemInfo.mainSystemInfo(CNTSuccessfulReq).toInt
      Thread.sleep(200) // will NOT make this block fail

      When("the request is sent")
      request ~> routes ~> check {

        Then("Status should be OK")
        status should === (StatusCodes.OK)

        // we expect the response to be json:
        contentType should ===(ContentTypes.`application/json`)

        Then("Statistics should be changed")
        var systemInfo = copyWithChage(
          f.currSystemInfo.mainSystemInfo,
          CNTSuccessfulReq,
          (currValue + 1).toString)

        entityAs[SystemInfo] should ===(systemInfo)
      }
    }
  }
}
