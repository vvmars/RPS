package design

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.concurrent.{ConcurrentHashMap, TimeUnit}

import Actors.DriverRPS
import Actors.DriverRPS.DrivingRPS
import akka.actor.{ActorRef, ActorSystem}
import com.typesafe.scalalogging.Logger
import config.Configuration
import org.slf4j.LoggerFactory
import servises.{CommonSla, Sla}

import scala.collection.JavaConversions._


/************************************************
  * case class for the container of the User
  */
case class UserRequest
(
  var rps: Float,
  var lastReqTime: LocalDateTime,
  var cntSuccessfulReq: Int = 0,
  var cntCancelledReq: Int = 0
)

object LocalCache {
  var users = new ConcurrentHashMap[String, Sla]
  var userRequests = new ConcurrentHashMap[Option[String], UserRequest]
}

class UserRequests {
  import UserRequests._

}

/*************************************************
  *
  */
object UserRequests extends Configuration {
  import LocalCache._
  private val logger = Logger(LoggerFactory.getLogger(this.getClass))
  //private val localCache: LocalCache = new LocalCache()
  private val NANOS = TimeUnit.SECONDS.toNanos(1)
  //-------------------------------------------------------
  // Create the actor system
  val system: ActorSystem = ActorSystem("ThrottlingService")
  // Create the driver actor
  val driverRPS: ActorRef = system.actorOf(DriverRPS.props, "driverRPSActor")
  //-------------------------------------------------------

  private def getFormatDateTime(dateTime: LocalDateTime): String = {
    dateTime.format(DateTimeFormatter.ofPattern("H:m:s:S:n"))
  }

  private def printUserRequests: Unit = {
    if (getEnv.equals(Constant.DefEnv))
      userRequests.keys().toList.foreach((key:Option[String]) => {
        val v = userRequests.get(key)
        logger.debug(s"ALL UserRequestS: size - ${userRequests.size()},  user - $key, rps - ${v.rps}, last time - ${getFormatDateTime(v.lastReqTime)}")
      })
  }

  private def printcurrentUserRequest(user: Option[String], currentUserRequest: UserRequest): Unit = {
    if (getEnv.equals(Constant.DefEnv)) {
      val time: String =
        currentUserRequest.lastReqTime match {
          case null => ""
          case (s) => getFormatDateTime(s)
        }
      logger.debug(s"Current UserRequest: user - $user, rps - ${currentUserRequest.rps}, last time - $time")
    }
  }

  /**
    * Return userName appropriate to token
    *
    * @param token - user's token
    * @return userName for given token if any
    */
  def getUserSla(token: String): Sla = {
    users.get(token)
  }

  def putUserName(token: Option[String], sal: Sla): Unit = {
    users.putIfAbsent(token.get, sal)
    logger.debug("Putting new user's SLA to cache: {} (all count in cache - {})", sal, users.size())
  }

  def increaseRPS(userRequest: UserRequest): Unit = {
    userRequest.rps = userRequest.rps * 1.1f
  }

  protected def updateCntSuccessTime(userRequest: UserRequest, lastReqTime: LocalDateTime): Unit = {
    this.synchronized{
      userRequest.lastReqTime = lastReqTime
      userRequest.cntSuccessfulReq += 1
    }
  }

  protected def updateCntCancel(userRequest: UserRequest, lastReqTime: LocalDateTime): Unit = {
    this.synchronized{
      userRequest.lastReqTime = lastReqTime
      userRequest.cntCancelledReq += 1
    }
  }

  /**
    * Check possibility to perform request
    * @param userRequest
    * @return sign of possibility to perform request
    */
  def checkRPS(userRequest: UserRequest): Boolean = {
    val timeNow: LocalDateTime = LocalDateTime.now()
    val reqTime: LocalDateTime =
      userRequest.lastReqTime match {
        case null => timeNow
        case _ => userRequest.lastReqTime
      }
    logger.debug(s"Checked time: reqTime - ${getFormatDateTime(reqTime)}, ${getFormatDateTime(timeNow)}, ${getFormatDateTime(reqTime.plusNanos(Math.round(NANOS / userRequest.rps)))}")
    if (reqTime.plusNanos(Math.round(NANOS / userRequest.rps))
      .isAfter(timeNow)
      ){
      updateCntCancel(userRequest, timeNow)
      driverRPS ! DrivingRPS(userRequest)
      false
    }
    else {
      updateCntSuccessTime(userRequest, timeNow)
      true
    }
  }

  /**
    * 1. Check USER in cache
    * 2. choose appropriate rps
    * 3. add new user to cache
    * @param commonSla - user's name
    * @return UserRequest for given user
    */
  private def getUserRequest(commonSla: CommonSla): UserRequest = {
    var rps: Int = 0
    //Check user in cache
    var userRequest: UserRequest = userRequests.get(commonSla.user)
    if (userRequest == null) {
      rps = commonSla.user match {
        case None => commonSla.graceRps
        case _ => commonSla.rps
      }
      //Add user to cache
      userRequest = UserRequest(rps, null, 1, 0)
      userRequests.putIfAbsent(commonSla.user, userRequest)
      printcurrentUserRequest(commonSla.user, userRequest)
    }
    userRequest
  }

  /**
    * Check RPS by user
    * @param commonSla - there is rps for authorized and unauthorized users
    * @return result of checking
    */
  def isRequestAllowed(commonSla: CommonSla): Boolean = {
    val currentUserRequest = getUserRequest(commonSla)
    checkRPS(currentUserRequest)
  }
}