package servises

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.{Failure, Success}

/**
  * todo
  * 1. to clean Maps ("token" and "userRequests") after disconn or idle interval
  *     the logic for "token" should takes into account whether appropriate userName
  *     is absent in the "userRequests"
  */
object RpsServices extends App {
  object localSlaService extends SlaService {
    def getSlaByToken(token: String): Future[Sla] =
      Future{
        //Thread.sleep(1000);
        token match {
          case "good" => Sla ("good_user", 10)
          case "bad" => Sla ("bad_user", 1)
        }
      }
  }
  val service = new ThrottlingServiceImp(localSlaService)
  val good: Option[String] = Some("good")
  val bad: Option[String] = Some("bad")
  service.isRequestAllowed(good)
  service.isRequestAllowed(bad)
  service.isRequestAllowed(good)
  service.isRequestAllowed(bad)
  Thread.sleep(1000)
  service.isRequestAllowed(good)
  service.isRequestAllowed(bad)

}
