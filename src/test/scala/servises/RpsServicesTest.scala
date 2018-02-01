package servises


import actors.RestActor
import org.scalatest._
import akka.actor.{ActorRef, ActorSystem, Props}
import akka.http.scaladsl.model.{ContentTypes, HttpRequest, StatusCodes}
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.testkit.ScalatestRouteTest
import config.Configuration
import design.SystemInfo
import org.scalatest.concurrent.ScalaFutures
import routers.ThrottlingRouters
import servises.accessory.{ThreadPoolService, ThrottlingServiceImpStub}

import scala.concurrent.duration.Duration


class RpsServicesTest
  extends FeatureSpec
    with Matchers
    with ScalaFutures
    with ScalatestRouteTest
    with GivenWhenThen
    with ThrottlingRouters
    with Configuration{

  import ThrottlingServiceImpStub._

  override val restActor: ActorRef =
    system.actorOf(RestActor.props, "restActor")

  lazy val routes: Route = throttlingRoutes
  //**************************************************
  def fixture =
    new {
      val currSystemInfo = getSystemInfo
      val pool = new ThreadPoolService
      val throttlingServiceImp = new ThrottlingServiceImpStub

    }
  //**************************************************
  feature("<F01>: Check ThrottlingService service") {
    scenario("<S01>: Overhead of using ThrottlingService service") {

      Given("1 user -> 1 request to Throttling service per 1 sec")
      val f = fixture
      val cntUsers: Int = 1
      val cntRps: Int = 1
      val durationSec: Int = 1
      val startTime = System.nanoTime()
      f.throttlingServiceImp.set(cntRps)

      When("Run pool and submit request")
      f.throttlingServiceImp.getInto(Some("good1"), 0)
      val endTime = System.nanoTime()

      info("Overhead of using ThrottlingService service:")
      info("Elepsited time - " + (endTime - startTime)/math.pow(10, 6) + " millis")
    }//<<scenario
    //------------------------------------------------
    scenario("<S02>: Perform 'blank' request (GET /sysinfo)") {

      Given("'blank' request and current statistics")
      val f = fixture
      val request = HttpRequest(uri = "/sysinfo")
      val startTime = System.nanoTime()

      When("the request is sent")

      request ~> routes ~> check {
        val endTime = System.nanoTime()

        info("Overhead of using ThrottlingService service:")
        info("Elepsited time - " + (endTime - startTime)/math.pow(10, 6) + " millis")

        Then("Status should be OK")
        status should ===(StatusCodes.OK)

        // we expect the response to be json:
        contentType should ===(ContentTypes.`application/json`)

        // and no entries should be in the list:
        entityAs[SystemInfo] should === (f.currSystemInfo)
      }
    }//<<scenario
    scenario("<S03>: Perform 1 requst") {

      Given("1 user -> 1 request to Throttling service per 1 sec")
      val f = fixture
      val cntUsers: Int = 1
      val cntRps: Int = 1
      val durationSec: Int = 1
      f.throttlingServiceImp.set(cntRps)

      When("Run pool and submit request")
      f.pool.submitThrottlingRequest(cntUsers, cntRps, durationSec, f.throttlingServiceImp)
      val currSystemInfo = f.throttlingServiceImp.getSystemInfoByUserRequests

      Then("Count of successfull processed request should be equal 1")
      assert( currSystemInfo.mainSystemInfo(CNTSuccessfulReq).toInt +
        currSystemInfo.mainSystemInfo(CNTCancelledReq).toInt === 1)
    }//<<scenario
    //------------------------------------------------
    scenario("<S04>: Perform request through REST endpoint with token (GET /token)") {

      Given("User request with token and current statistics")
      val f = fixture
      val request = Get("/sysinfo/good1")
      val currValue: Int = f.currSystemInfo.mainSystemInfo(ThrottlingServiceImpStub.CNTSuccessfulReq).toInt

      When("the request is sent")
      //Thread.sleep(200) // will NOT make this block fail
      request ~> routes ~> check {

        Then("Status should be OK")
        status should ===(StatusCodes.OK)

        Then("Statistics should be changed")
        var systemInfo = copyWithChage(
          f.currSystemInfo.mainSystemInfo,
          ThrottlingServiceImpStub.CNTSuccessfulReq,
          (currValue + 1).toString)

        entityAs[SystemInfo] should ===(systemInfo)
      }
    }//<<scenario
    //------------------------------------------------
    scenario("<S03>: Perform N for  requsts") {

      Given("1 user -> 1 request to Throttling service per 1 sec")
      val f = fixture
      val cntUsers: Int = 3
      val cntRps: Int = getRps
      val durationSec: Int = 10
      f.throttlingServiceImp.set(cntRps)

      When("Run pool and submit request")
      f.pool.submitThrottlingRequest(cntUsers, cntRps, durationSec, f.throttlingServiceImp)
      val currSystemInfo = f.throttlingServiceImp.getSystemInfoByUserRequests

      Then("Count of successfull processed request should be equal 1")
      assert( currSystemInfo.mainSystemInfo(CNTSuccessfulReq).toInt +
        currSystemInfo.mainSystemInfo(CNTCancelledReq).toInt === 1)
    }//<<scenario
  }


}
