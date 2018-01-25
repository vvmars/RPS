package design

import java.util.concurrent.ConcurrentHashMap

/**
  *
  */
object ThrottlingRequest {
  private var userRequests: UserRequests = _
  private var users = new ConcurrentHashMap[String, Option[String]]

  /**
    *
    * @param user
    * @param rps
    * @return
    */
  private def isRequestAllowedByUser(user: Option[String], rps:Int): Boolean = {
    user match {
      case Some(s) => userRequests = new AuthorizedUserRequests
      case None => userRequests = new UnAuthorizedUserRequests
    }
    userRequests.isRequestAllowedByUser(user, rps)
  }

  /**
    * Return userName appropriate to token
    * @param token - user's token
    * @return userName for given token if any
    */
  def getUserName(token: Option[String]): Option[String] = {
    token match {
      case Some(s) => users.get(s)/*{
        val usr = users.get(s)
        if (usr == null)
          None
        else usr
      }*/
      case None => None
    }
  }

  def putUserName(token: Option[String], user: Option[String]) = {
    users.putIfAbsent(token.get, user)
  }

  def isRequestAllowed(token:Option[String], rps: Int): Boolean = {
    val usr = getUserName(token)
    isRequestAllowedByUser(usr, rps)
  }

}
