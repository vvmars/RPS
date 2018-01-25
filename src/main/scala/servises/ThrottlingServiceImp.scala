package servises

import com.typesafe.scalalogging._
import config.Configuration
import design.ThrottlingRequest
import org.slf4j.LoggerFactory
import scala.concurrent.Future

class ThrottlingServiceImp extends ThrottlingService with Configuration {
  private val logger = Logger(LoggerFactory.getLogger(this.getClass))

  var graceRps: Int = getGraceRps

  logger.info("service started")

  // There should be invocation of method "getSlaByToken" from SlaService
  // sla = getSlaByToken(user, rps)
  // but currently there is no implementation
  /*val slaService: SlaService = {
    Future[Sla]
  }*/

  def isRequestAllowed(token:Option[String]): Boolean = {
    val user = ThrottlingRequest.getUserName(token)
    if (token.isDefined & user.isEmpty) {
      //!!!!!!!!!!!!!!!!!!!!!!!!!!!!
      //Run service
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
