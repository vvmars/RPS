package servises

import com.typesafe.scalalogging._
import config.Configuration
import design.ThrottlingRequest
import org.slf4j.LoggerFactory

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.{Await, Future}
import scala.util.{Failure, Success, Try}
import scala.concurrent.duration._

class ThrottlingServiceImp(val slaService: SlaService) extends ThrottlingService with Configuration {
  private val logger = Logger(LoggerFactory.getLogger(this.getClass))

  logger.info("service started")

  var graceRps: Int = getGraceRps
/*  def setService (slaService: SlaService): Unit = {
     this.slaService = slaService
  }*/

  def slaResult(sla: Sla): Sla = {
    sla
  }

  private def getSla(token: Option[String]): Unit = {
    val res = Future{slaService.getSlaByToken(token.get)}
    res onComplete {
      case Success(res: Sla) => slaResult(res)
      case Failure(t: Throwable) => println(s"There is no user by provided token: $t")
    }
  }

  def isRequestAllowed(token:Option[String]): Boolean = {
    var user = ThrottlingRequest.getUserName(token)
    if (token.isDefined & user == null) {
      //!!!!!!!!!!!!!!!!!!!!!!!!!!!!
      //Run service
      val sla = Future{slaService.getSlaByToken(token.get)}
      //users.put(token.get, Some("WWW"))
      ThrottlingRequest.putUserName(token, user)
    }
    val localRps = user match {
      case Some(s) => getRps
      case _ => graceRps
    }
    ThrottlingRequest.isRequestAllowed(user, localRps)
  }
}
