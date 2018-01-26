package servises

import com.typesafe.scalalogging._
import design.ThrottlingRequest
import org.slf4j.LoggerFactory

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.{Failure, Success, Try}
import config.Configuration
//import scala.concurrent.duration._

class ThrottlingServiceImp(val slaService: SlaService) extends ThrottlingService with Configuration {
  private val logger = Logger(LoggerFactory.getLogger(this.getClass))
  //private var sla: Sla
  logger.info("service started")
  //only to make this class to not be abstract
  var graceRps: Int = getGraceRps

  def processSla(token: Option[String], sla: Sla): Unit = {
    ThrottlingRequest.putUserName(token, Some(sla.user))
  }

  private def requestSlaService(token: Option[String]): Unit = {
    val res = Future{slaService.getSlaByToken(token.get)}
    res onComplete {
      case Success(res: Sla) => processSla(token, res)
      case Failure(t: Throwable) => println(s"There is no user by provided token: $t")
      case _ => logger.info(s"Sla future error: %t")
    }
  }

  private def getSla(token:Option[String]): CommonSla = {
    var commonSla: CommonSla = null// = CommonSla(null, 0, graceRps)
    var user: Option[String] = None
    if (token.isDefined) {
      //Check cash
      user = ThrottlingRequest.getUserName(token.get)
      if (user == null) {
        //Run service to identify sla
        requestSlaService(token)
        //??? How can we identify if the service is completed
        user = ThrottlingRequest.getUserName(token.get)
        if (user != null) {
          commonSla = CommonSla(user, 0, getRps)
        }
      }
    }
    if ((token.isDefined) || user == None)
      commonSla = CommonSla(None, 0, graceRps)
    commonSla
  }

  def isRequestAllowed(token:Option[String]): Boolean = {
    val commonSla = getSla(token)
    ThrottlingRequest.isRequestAllowed(commonSla)
  }
}
