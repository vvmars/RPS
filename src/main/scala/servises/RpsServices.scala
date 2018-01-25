package servises

/**
  * todo
  * 1. to clean Maps ("token" and "userRequests") after disconn or idle interval
  *     the logic for "token" should takes into account whether appropriate userName
  *     is absent in the "userRequests"
  */
object RpsServices extends App {
  val service = new ThrottlingServiceImp()
  val v: Option[String] = Some("w")
  service.isRequestAllowed(v)
  service.isRequestAllowed(v)
}
