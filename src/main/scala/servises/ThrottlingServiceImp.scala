package servises

import com.typesafe.scalalogging._
import design.{Constant, UserRequests}
import org.slf4j.LoggerFactory
import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.{Failure, Success}
import config.Configuration


class ThrottlingServiceImp(val slaService: SlaService) extends ThrottlingService with Configuration {
  private val logger = Logger(LoggerFactory.getLogger(this.getClass))

  logger.info(Constant.RootDelimiter)
  logger.info("SERVICE STARTED")
  logger.info(Constant.RootDelimiter)
  //only to make this class to not be abstract
  var graceRps: Int = getGraceRps

  def processSla(token: Option[String], sla: Sla): Unit = {
    if (sla != null & sla.user != null) {
      logger.debug("Sla service provided user's rps: user - {}, rps - {}", sla.user, sla.rps)
      UserRequests.putUserName(token, sla)
    }
    else logger.debug("Bad responce on token: {}", token)

  }

  private def requestSlaService(token: Option[String]): Unit = {
    val res = slaService.getSlaByToken(token.get)
    res onComplete {
      case Success(resSla: Sla) => processSla(token, resSla)
      case Failure(t: Throwable) => logger.debug(s"There is no user by provided token: $t")
      case _ => logger.debug(s"Sla future error: %t")
    }
  }

  private def getSla(token:Option[String]): CommonSla = {
    var commonSla: CommonSla = null
    var sla: Sla = null
    if (token.isDefined) {
      //Check cache
      sla = UserRequests.getUserSla(token.get)
      if (sla == null) {
        //Run service to identify sla
        requestSlaService(token)
        //??? How can we identify if the service is completed
        sla = UserRequests.getUserSla(token.get)
        if (sla != null) {
          commonSla = CommonSla(Some(sla.user), 0, sla.rps)
          logger.debug("User from cache - " + sla.user)
        }
        else sla = null
      }
    }
    if (token.isEmpty || sla == null) {
      commonSla = CommonSla(None, 0, getRps)
      logger.debug("unAuthorized user")
    }
    else
      commonSla = CommonSla(None, 0, graceRps)
    commonSla
  }

  def isRequestAllowed(token:Option[String]): Boolean = {
    val commonSla = getSla(token)
    UserRequests.isRequestAllowed(commonSla)
  }
}
