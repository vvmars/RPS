package servises

import java.time.LocalDateTime

import Actors.DriverRPS
import Actors.DriverRPS.DrivingRPS
import org.scalatest.{BeforeAndAfterAll, FlatSpecLike, FunSuite, Matchers}
import akka.actor.{Actor, ActorRef, ActorSystem, Props}
import akka.testkit.{ImplicitSender, TestActorRef, TestKit, TestProbe}
import design.{UserRequest, UserRequests}
import design.UserRequests.{driverRPS, system}
//import org.scalatest.mock.MockitoSugar
import org.scalatest.mockito.MockitoSugar
import org.mockito.Mockito._

import scala.concurrent.duration._

//#test-classes
class class RpsActorsTest (_system: ActorSystem)
  extends TestKit(_system)
    with Matchers
    with BeforeAndAfterAll
    with FunSuite
    with FlatSpecLike
    with MockitoSugar {
  //#test-classes
  def this() = this(ActorSystem("RpsActorsTest"))

  override def afterAll: Unit = {
    shutdown(system)
  }

  val userRequest = UserRequest(99, LocalDateTime.now(), 2, 3)

  //var m = mock[UserRequests]
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
    val testProbe = TestProbe()
    val helloGreetingMessage = "hello"
    val driverRPS: ActorRef = system.actorOf(DriverRPS.props, "driverRPSActor")
    driverRPS ! DrivingRPS(userRequest)
    testProbe.expectMsg(500 millis, Greeting(s"$helloGreetingMessage, $greetPerson"))
  }
  //#first-test
}
//#full-example

