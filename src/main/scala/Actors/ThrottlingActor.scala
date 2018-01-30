package Actors

import design.UserRequests
import akka.actor.{Actor, ActorLogging, Props}

object RestActor {
  final case class GetInfo(token: Option[String])
  final case class GetCheckedInfo(token: Option[String])

  def props: Props = Props[RestActor]
}

class RestActor extends Actor with ActorLogging {
  import RestActor._

  def receive: Receive = {
    case GetInfo(token) =>
      sender() ! UserRequests.getInto(token)
    case GetCheckedInfo(token) =>
      sender() ! UserRequests.getInto(token)
  }
}
