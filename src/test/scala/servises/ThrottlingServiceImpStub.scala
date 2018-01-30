package servises

import design.SystemInfo

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

class ThrottlingServiceImpStub {
  val service = new ThrottlingServiceImp(localSlaService)

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

  private def getSystemInfo: SystemInfo = {
    val systemInfo = SystemInfo(0)

    systemInfo
  }

  def getInto(token: Option[String]): SystemInfo = {
    Thread.sleep(5)

    getSystemInfo
  }

  def getCheckedInfo(token: Option[String]): SystemInfo = {
    val isRequestAllowed = service.isRequestAllowed(token)
    if (isRequestAllowed)
      getSystemInfo
    else null
  }
}

