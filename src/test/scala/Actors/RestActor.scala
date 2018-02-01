package actors

import design.{SystemInfo, UserRequests}
import akka.actor.{Actor, ActorLogging, Props}
import servises.accessory.ThrottlingServiceImpStub

object RestActor {
  final case class GetInfo(token: Option[String])

  val throttlingServiceImpStub = new ThrottlingServiceImpStub()

  def props: Props = Props[RestActor]
}

class RestActor extends Actor with ActorLogging {
  import RestActor._

  def receive: Receive = {
    case GetInfo(token) =>
      val systemInfo = throttlingServiceImpStub.getInto(token)
      sender() ! systemInfo
  }
}