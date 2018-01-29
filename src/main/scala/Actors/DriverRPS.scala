package Actors

import akka.actor.{Actor, ActorLogging, Props, Timers}
import akka.event.Logging
import design.{UserRequest, UserRequests}

import scala.concurrent.duration._

//#will increase rps
object DriverRPS {
  //#printer-messages
  def props: Props = Props[DriverRPS]
  //#printer-messages
  final case class DrivingRPS(userRequest: UserRequest)
}
//#will increase rps

//#printer-actor
class DriverRPS extends Actor with ActorLogging with Timers {
  import DriverRPS._
  timers.startSingleTimer("DrivingRPS", DrivingRPS, 100.millis)

  def receive = {
    case DrivingRPS(userRequest) => {
      UserRequests.increaseRPS(userRequest)
      log.info(s"Increasing RPS for user: $userRequest.")
    }
  }
}
