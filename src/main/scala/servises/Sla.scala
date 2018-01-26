package servises

import scala.concurrent.Future

case class Sla(user: String, rps: Int)
case class CommonSla(user: Option[String], rps: Int, graceRps: Int)

trait SlaService {
  def getSlaByToken(token: String): Future[Sla]
}
