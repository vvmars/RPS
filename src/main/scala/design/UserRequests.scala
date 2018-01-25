package design

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
abstract class UserRequests{
  protected var currentUser: UserRequest = _
  /**
    * Set common RPS parameter
    * @param rps - rps for authorized user's requests
    */
  protected def setRps(rps: Int): Unit = {
    currentUser.rps = rps
  }

  protected def increaseRPS(userRequest: UserRequest): Unit = {
    userRequest.cntReq += 1
  }

  /**
    *
    * @param userRequest
    * @return
    */
  protected def checkRPS(userRequest: UserRequest): Boolean = {
    var res: Boolean = false
    if (userRequest.cntReq < userRequest.rps){
      increaseRPS(userRequest)
      res = true
    }
    res
  }

  def isRequestAllowedByUser(user: Option[String], rps: Int): Boolean
}