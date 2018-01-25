package design

import java.util.concurrent.ConcurrentHashMap

class AuthorizedUserRequests extends UserRequests {
  private var authorizedUsers = new ConcurrentHashMap[Option[String], UserRequest]

  /**
    * Return UserRequest appropriate to user
    * @param user - user's name
    * @return UserRequest for given user
    */
  private def getUserRequest(user: Option[String]): UserRequest ={
    var userRequest: UserRequest = authorizedUsers.get(user)
    if (userRequest == null)
      userRequest = authorizedUsers.putIfAbsent(user, UserRequest(0, 0))
    userRequest
  }

  /**
    * Check RPS by user
    * @param user
    * @param rps
    * @return
    */
  def isRequestAllowedByUser(user: Option[String], rps:Int): Boolean = {
    currentUser = getUserRequest(user)
    setRps(rps)
    checkRPS(currentUser)
  }
}
