package servises

import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.function.LongBinaryOperator
import java.util.stream.IntStream
import java.time.LocalDateTime

import actors.{DriverRPS, RestActor}
import actors.DriverRPS.DrivingRPS
import org.scalatest._
import akka.actor.{Actor, ActorRef, ActorSystem, Props}
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.testkit.ScalatestRouteTest
import akka.testkit.{ImplicitSender, TestActorRef, TestKit, TestKitBase, TestProbe}
import design.{UserRequest, UserRequests}
import org.mockito.Mockito._
import org.scalatest.concurrent.ScalaFutures
import routers.ThrottlingRouters
import servises.accessory.ThrottlingServiceImpStub

class RpsServicesTest(_system: ActorSystem)
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
  def fixture =
    new {
      val currSystemInfo = ThrottlingServiceImpStub.getSystemInfo
    }
  //**************************************************
  def fixture =
    new {
      val currSystemInfo = ThrottlingServiceImpStub.getSystemInfo
    }
  //**************************************************



  val op: LongBinaryOperator = (x: Long, y: Long) => 2 * x + y
  val accumulator = new Nothing(op, 1L)

  val executor: ExecutorService = Executors.newFixedThreadPool(2)

  IntStream.range(0, 10).forEach((i: Int) => executor.submit(() => accumulator.accumulate(i)))

  stop(executor)

  System.out.println(accumulator.getThenReset) // => 2539


}
