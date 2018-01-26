package design

import java.util.concurrent.ConcurrentHashMap
import servises.CommonSla

/**
  *
  */
object ThrottlingRequest {
  private val userRequests: UserRequests = new UserRequests()
  private val users = new ConcurrentHashMap[String, Option[String]]

  /**
    * Return userName appropriate to token
    * @param token - user's token
    * @return userName for given token if any
    */
  def getUserName(token: String): Option[String] = {
    users.get(token)
  }

  def putUserName(token: Option[String], user: Option[String]) = {
    users.putIfAbsent(token.get, user)
    /*for (s <- users)
      print(s + " - " )*/
  }

  /**
    *
    * @param user
    * @param rps
    * @return
    */
/*  private def isRequestAllowedByUser(user: Option[String], rps:Int): Boolean = {
    user match {
      case Some(s) => userRequests = new AuthorizedUserRequests
      case None => {
        userRequests = new UnAuthorizedUserRequests
        userRequests.setCurrentUser(unAuthorizedUserRequest)
      }
    }
    userRequests.isRequestAllowedByUser(user, rps)
  }*/



  def isRequestAllowed(commonSla: CommonSla): Boolean = {
    /*sla.user match {
      case Some(s) => userRequests = new AuthorizedUserRequests
      case None => userRequests = new UnAuthorizedUserRequests
    }*/
    userRequests.isRequestAllowedByUser(commonSla)
  }

  /*  def isRequestAllowed(token:Option[String], rps: Int): Boolean = {
    val usr = getUserName(token)
    isRequestAllowedByUser(usr, rps)
  }*/

/*
  def isRequestAllowed(token:Option[String]): Boolean = {
    var user = ThrottlingRequest.getUserName(token)
    if (token.isDefined & user == null) {
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
  }*/

}
