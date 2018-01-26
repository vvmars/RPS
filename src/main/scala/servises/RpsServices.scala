package servises

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

/**
  * todo
  * 1. to clean Maps ("token" and "userRequests") after disconn or idle interval
  *     the logic for "token" should takes into account whether appropriate userName
  *     is absent in the "userRequests"
  */
object RpsServices extends App {

  object localSlaService extends SlaService {
    def getSlaByToken(token: String): Future[Sla] =
      Future{Thread.sleep(1000);Sla("test", 10)}
  }
  val service = new ThrottlingServiceImp(localSlaService)
  val v: Option[String] = Some("w")
  service.isRequestAllowed(v)
  service.isRequestAllowed(v)
}
