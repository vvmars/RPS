package servises.accessory

import design.SystemInfo
import servises.{Sla, SlaService, ThrottlingServiceImp}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class ThrottlingServiceImpStub {
  import ThrottlingServiceImpStub._
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
    val systemInfo = startSystemInfo

    systemInfo
  }

  def getInto(token: Option[String]): SystemInfo = {
    Thread.sleep(5)
    println(token)
    token match {
      case None | Some("*") => SystemInfo(-1)
      case _ => getSystemInfo
    }
  }

  def getCheckedInfo(token: Option[String]): SystemInfo = {
    val isRequestAllowed = service.isRequestAllowed(token)
    if (isRequestAllowed)
      getSystemInfo
    else emptySystemInfo
  }
}

object ThrottlingServiceImpStub{
  val emptySystemInfo = SystemInfo(-1)
  val startSystemInfo = SystemInfo(0)
}
