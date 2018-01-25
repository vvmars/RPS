package design

class UnAuthorizedUserRequests extends UserRequests {
  currentUser = UserRequest(0, 0)

  def isRequestAllowedByUser(user: Option[String], rps:Int): Boolean = {
    setRps(rps)
    checkRPS(currentUser)
  }
}
