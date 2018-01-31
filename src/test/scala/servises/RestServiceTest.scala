package servises

import actors.RestActor
import routers.ThrottlingRouters
import akka.actor.ActorRef
import akka.http.scaladsl.marshalling.Marshal
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

  override val restActor: ActorRef =
    system.actorOf(RestActor.props, "userRegistry")

  lazy val routes: Route = throttlingRoutes
  //**************************************************
  feature("Check REST endpoint") {
    scenario("Perform 'blank' request (GET /sysinfo)") {

      Given("'blank' request")
      val request = HttpRequest(uri = "/sysinfo")

      When("the request is sent")

      request ~> routes ~> check {

        Then("Status should be OK")
        status should ===(StatusCodes.OK)

        // we expect the response to be json:
        contentType should ===(ContentTypes.`application/json`)

        // and no entries should be in the list:
        entityAs[SystemInfo] should ===(ThrottlingServiceImpStub.emptySystemInfo)
      }
    }
    //------------------------------------------------
    scenario("Perform request with token (GET /token)") {

      Given("User request with token")
      //val userEntity = Marshal("userToken").to[MessageEntity].futureValue
      // using the RequestBuilding DSL:
      //val request = Get("/sysinfo").withEntity(userEntity)
      val request = Get("/sysinfo/userToken")

      When("the request is sent")
      request ~> routes ~> check {

        Then("Status should be OK")
        status should ===(StatusCodes.OK)

        // we expect the response to be json:
        contentType should ===(ContentTypes.`application/json`)

        // and we know what message we're expecting back:
        entityAs[SystemInfo] should ===(ThrottlingServiceImpStub.startSystemInfo)
      }
    }
  }
}
