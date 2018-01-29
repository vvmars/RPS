package servises

import java.time.LocalDateTime

import Actors.DriverRPS
import Actors.DriverRPS.DrivingRPS
import org.scalatest._
import akka.actor.{Actor, ActorRef, ActorSystem, Props}
import akka.testkit.{ImplicitSender, TestActorRef, TestKit, TestKitBase, TestProbe}
import design.{UserRequest, UserRequests}
import design.UserRequests.{driverRPS, system}
import org.mockito.Mockito
import org.mockito.invocation.InvocationOnMock
import org.mockito.stubbing.Answer
//import org.scalatest.mock.MockitoSugar
import org.scalatest.mockito.MockitoSugar
import org.mockito.Mockito._
import org.mockito.Matchers.any

import scala.concurrent.duration._

//#test-classes
/*
class RpsActorsTest (_system: ActorSystem)
  extends TestKit(_system)
    with Matchers
    //with FunSuite
    with FlatSpecLike
    with BeforeAndAfterAll
    with MockitoSugar {
  //#test-classes

  def this() = this(ActorSystem("RpsActorsTest"))

  override def afterAll: Unit = {
    shutdown(system)
  }

  def fixture =
    new {
      val userRequest = UserRequest(99, LocalDateTime.now(), 2, 3)
      var spyUserRequest = spy(UserRequests)
    }



  /*def spyincreaseRPS(userRequest: UserRequest): Unit = {
    userRequest.rps = userRequest.rps * 1.1f
  }*/

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
    when(service.login("joehacker", "secret")).thenReturn(None)

    // (3) access the service
    val johndoe = service.login("johndoe", "secret")
    val joehacker = service.login("joehacker", "secret")

    // (4) verify the results
    assert(johndoe.get == User("johndoe"))
    assert(joehacker == None)
  * */

  //#first-test
  //#specification-example
  "A Greeter Actor" should "pass on a greeting message when instructed to" in {
    //#specification-example
    val f = fixture
    val testProbe = TestProbe()
    val helloGreetingMessage = "hello"
    val driverRPS: ActorRef = system.actorOf(DriverRPS.props, "driverRPSActor")
    driverRPS ! DrivingRPS(f.userRequest)
    //testProbe.expectMsg(500 millis, Greeting(s"$helloGreetingMessage, $greetPerson"))
    //testProbe.expectMsg(userRequest)
  }
  //#first-test
}
//#full-example

*/


class ww extends FeatureSpec with GivenWhenThen

class RpsActorsTest (_system: ActorSystem)
  extends TestKit(_system)
    with Matchers
    with FlatSpecLike//FeatureSpecLike
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
      val cntSuccessfulReq = 1
      val userRequest = UserRequest(rps, dtNow, cntSuccessfulReq, 0)
      //var spyUserRequest = spy(UserRequests)
    }

  /*feature("Check Driver Actor") {
    scenario("Send current state of rps to increase it") {

      Given("current state of rps")
        val f = fixture
        val driverActor = system.actorOf(DriverRPS.props)
        assert(fixture != null)
      When("the message is sent")
        driverActor ! DrivingRPS(f.userRequest)
      Then("the TV should switch on")
        //assert(f.userRequest.rps == f.rps * 1.1f)
      println(s"1 - ${f.userRequest.rps} 2 - ${f.rps}")
    }
  }*/

  "A Greeter Actor" should "pass on a greeting message when instructed to" in {
    val f = fixture
    val driverActor = system.actorOf(DriverRPS.props)
    //var expectUserRequest = UserRequest(0,null, 0,0)
    driverActor ! DrivingRPS(f.userRequest)
    Thread.sleep(1000)
    println(s"1 - ${f.userRequest.rps} 2 - ${f.rps}")
    //expectMsg(1 seconds, f.userRequest)
    println(s"1 - ${f.userRequest}")
  }
}
