package servises.accessory

import actors.RestActor
import akka.actor.{ActorRef, ActorSystem}
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Route
import akka.stream.ActorMaterializer
import routers.ThrottlingRouters

import scala.concurrent.Await
import scala.concurrent.duration.Duration

object RestService extends App with ThrottlingRouters {

  // set up ActorSystem and other dependencies here
  //>>server-bootstrapping
  implicit val system: ActorSystem = ActorSystem("ThrottlingHttpServer")
  implicit val materializer: ActorMaterializer = ActorMaterializer()
  //<<server-bootstrapping

  val restActor: ActorRef = system.actorOf(RestActor.props, "RestActor")

  lazy val routes: Route = throttlingRoutes

  //>http-server
  Http().bindAndHandle(routes, "localhost", 8080)

  println(s"Server online at http://localhost:8080/")

  Await.result(system.whenTerminated, Duration.Inf)
}