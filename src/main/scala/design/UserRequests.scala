package design

import java.util.concurrent.ConcurrentHashMap
//import collection.JavaConverters._
//import scala.collection.mutable._//ConcurrentMap


import servises.CommonSla

/**
  * case class for the container of the User
  */
case class UserRequest
(
  var rps: Int,
  //lastReqTime: LocalTime,
  var cntReq: Int
)

/**
  *
  */
class UserRequests{
  protected var currentUserRequest: UserRequest = _
  private val userRequests = new ConcurrentHashMap[Option[String], UserRequest]
/*  /**
    * Set common RPS parameter
    * @param rps - rps for authorized user's requests
    */
  protected def setRps(rps: Int): Unit = {
    currentUserRequest.rps = rps
  }*/

  protected def increaseRPS(userRequest: UserRequest): Unit = {
    userRequest.cntReq += 1
  }

  /**
    * Check possibility to perform request
    * @param userRequest
    * @return sign of possibility to perform request
    */
  protected def checkRPS(userRequest: UserRequest): Boolean = {
    var res: Boolean = false
    if (userRequest.cntReq < userRequest.rps){
      increaseRPS(userRequest)
      res = true
    }
    res
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
      userRequest = userRequests.putIfAbsent(commonSla.user, UserRequest(rps, 0))


      /*scala.util.mutable.ConcurrentMap
      userRequests.toMap*/
      /*for (Iterator i = userRequests.iterator(); i.hasNext(); ) {
        System.out.println(i.next().toString());
      }
        print(s.user + " - " )*/
    }
    userRequest
  }

  /**
    * Check RPS by user
    * @param commonSla - there is rps for authorized and unauthorized users
    * @return result of checking
    */
  def isRequestAllowedByUser(commonSla: CommonSla): Boolean = {
    currentUserRequest = getUserRequest(commonSla)
    checkRPS(currentUserRequest)
  }
}