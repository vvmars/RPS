package servises

import java.time.LocalDateTime
import Actors.DriverRPS
import Actors.DriverRPS.DrivingRPS
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

  /*when(spyUserRequest.increaseRPS(any)).thenAnswer(new Answer[Unit] {
    override def answer(invocation: InvocationOnMock): Unit = {
      //return something
    }
  }*/
/*  when(spyUserRequest.increaseRPS(any[UserRequest])).thenAnswer(
    new Answer[Unit] {
      override def answer(invocation: InvocationOnMock): Unit = {
        //spyUserRequest.userRequest
        return Unit
      }
    })*/


  /*
  *     when(service.login("johndoe", "secret")).thenReturn(Some(User("johndoe")))
  * */

*/
class RpsActorsTest (_system: ActorSystem)
  extends TestKit(_system)
    with Matchers
    with /*FlatSpecLike*/FeatureSpecLike
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
      val userRequest = UserRequest(rps, dtNow, cntSuccessfulReq, cntCancelledReq)
      var spyUserRequest = spy(new UserRequests())
    }

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
    scenario("Increasing cont of successful requests") {

      Given("current state of rps")
      var f = fixture
      f.userRequest.lastReqTime = LocalDateTime.now()
      assert(fixture != null)

      When("the message is sent")
      val res = f.spyUserRequest.checkRPS(f.userRequest)
      Thread.sleep(200) // will NOT make this block fail

      Then("Count requests should be increasing")
      assert(f.userRequest.cntCancelledReq == f.cntCancelledReq + 1)
    }
  }
}
