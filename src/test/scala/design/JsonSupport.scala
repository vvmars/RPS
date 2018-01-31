package design

import actors.RestActor.{GetCheckedInfo, GetInfo}
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import spray.json.DefaultJsonProtocol

trait JsonSupport extends SprayJsonSupport {
  // import the default encoders for primitive types (Int, String, Lists etc)
  import DefaultJsonProtocol._

  implicit val getInfoJsonFormat = jsonFormat1(SystemInfo)

}
