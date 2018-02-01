package servises

import java.time.LocalDateTime
import java.util.concurrent.atomic.AtomicInteger

import actors.DriverRPS
import actors.DriverRPS.DrivingRPS
import org.scalatest._
import akka.actor.{Actor, ActorRef, ActorSystem, Props}
import akka.testkit.{ImplicitSender, TestActorRef, TestKit, TestKitBase, TestProbe}
import design.{UserRequest, UserRequests}
import org.mockito.Mockito._
//import org.mockito.Mockito
//import org.mockito.invocation.InvocationOnMock
//import org.mockito.stubbing.Answer
//import org.scalatest.mock.MockitoSugar
//import org.scalatest.mockito.MockitoSugar
//import org.mockito.Matchers.any
//import scala.concurrent.duration._

/*
when(service.login("", "")).thenReturn(Some(User("")))
when(spyUserRequest.increaseRPS(any[UserRequest])).thenAnswer(
    new Answer[Unit] {
      override def answer(invocation: InvocationOnMock): Unit = {
        //spyUserRequest.userRequest
        return Unit
      }
    })*/

class RpsActorsTest (_system: ActorSystem)
  extends TestKit(_system)
    with Matchers
    with FeatureSpecLike
    with GivenWhenThen
    with BeforeAndAfterAll {

  def this() = {
    this(ActorSystem("RpsActorsTest"))
  }

  override def afterAll: Unit = {
    shutdown(system)
  }

  def fixture =
    new {
      val dtNow = LocalDateTime.now()
      val rps: Float = 100
      val cntSuccessfulReq = 0
      val cntCancelledReq = 0
      val userRequest = UserRequest(None, rps, dtNow, cntSuccessfulReq, cntCancelledReq)
      var spyUserRequest = new UserRequests()
    }
  //**************************************************
  feature("Check Driver Actor") {
    scenario("Send current state of rps to increase it") {

      Given("current state of rps")
      val f = fixture
      val driverActor = system.actorOf(DriverRPS.props)
      assert(f.userRequest != null)

      When("the message is sent")
      driverActor ! DrivingRPS(f.userRequest, f.spyUserRequest.increaseRPS _)
      Thread.sleep(200) // will NOT make this block fail

      Then("Rps should be increasing")
      assert(f.userRequest.rps == f.rps * 1.1f)
    }
    //------------------------------------------------
    scenario("Perform check of RPS to increase it") {

      Given("current state of rps")
      var f = fixture
      f.userRequest.lastReqTime = LocalDateTime.now()
      assert(fixture != null)

      When("the message is sent twice")
      var res = f.spyUserRequest.checkRPS(f.userRequest)
      res = f.spyUserRequest.checkRPS(f.userRequest)
      Thread.sleep(200) // will NOT make this block fail

      Then("Count requests should be increasing")
      assert(f.userRequest.cntCancelledReq == f.cntCancelledReq + 1)
    }
  }
}
