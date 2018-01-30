package Routers

import Actors.RestActor.GetInfo

import scala.concurrent.duration._
import akka.actor.{ActorRef, ActorSystem}
import akka.event.Logging
import akka.http.scaladsl.server.PathMatcher
import akka.http.scaladsl.server.Directives.{as, concat, entity, onSuccess, pathEnd, pathPrefix, rejectEmptyResponse}
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.server.directives.MethodDirectives.get
import akka.http.scaladsl.server.directives.PathDirectives.path
import akka.http.scaladsl.server.directives.RouteDirectives.complete
import akka.util.Timeout
import design.{JsonSupport, SystemInfo}
import scala.concurrent.Future
import akka.pattern.ask

trait ThrottlingRouters extends JsonSupport {

  implicit def system: ActorSystem

  lazy val log = Logging(system, classOf[ThrottlingRouters])

  def restActor: ActorRef

  // Required by the `ask` (?) method below
  implicit lazy val timeout = Timeout(5.seconds)

  lazy val throttlingRoutes: Route =
  pathPrefix("sysinfo") {
    concat(
      pathEnd {
        concat(
          get {
            val sysInfo: Future[Option[SystemInfo]] =
              (restActor ? GetInfo(Some("dd"))).mapTo[Option[SystemInfo]]
            complete(sysInfo)
          }
        )
      },
      path(Segment) { token =>
        concat(
          get {
            val sysInfo: Future[Option[SystemInfo]] =
              (restActor ? GetInfo(Some(token))).mapTo[Option[SystemInfo]]
            rejectEmptyResponse {
              complete(sysInfo)
            }
          }
        )
      }
    )
  }
}
