package routers



import scala.concurrent.duration._
import akka.actor.{ActorRef, ActorSystem}
import akka.event.Logging
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.{RejectionHandler, Route, ValidationRejection}
import akka.http.scaladsl.server.directives.MethodDirectives.get
import akka.http.scaladsl.server.directives.PathDirectives.path
import akka.http.scaladsl.server.directives.RouteDirectives.complete
import akka.util.Timeout
import design.{JsonSupport, SystemInfo}
import actors.RestActor.GetInfo
import akka.http.scaladsl.model.StatusCodes

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
            val sysInfo: Future[SystemInfo] =
              (restActor ? GetInfo(None)).mapTo[SystemInfo]
            //complete(sysInfo)
            onSuccess(sysInfo) { performed =>
              log.info("GET [{}]", sysInfo)
              complete((StatusCodes.OK, performed))
            }
          }
        )
      },
      path(Segment) { token =>
        concat(
          get {
            val sysInfo: Future[SystemInfo] =
              (restActor ? GetInfo(Some(token))).mapTo[SystemInfo]
            rejectEmptyResponse {
              complete(sysInfo)
            }
          }
        )
      }
    )
  }
}
