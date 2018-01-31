package servises.accessory

import java.time.LocalDateTime
import design.SystemInfo
import servises.{Sla, SlaService, ThrottlingServiceImp}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class ThrottlingServiceImpStub {

  import ThrottlingServiceImpStub._

  set
  //println(tokens)

  def set: Unit = {
    for (i <- 1 to tokenX) {
      for (t <- tokenName) {
        tokens += (Some(t + i.toString) -> Sla(t + "_user" + i.toString, i * 2))
      }
    }
  }

  def getToken: Option[String] = {
    val randomtokenX: Int = new scala.util.Random(tokenName.length).nextInt()
    val randomtokenY: Int = new scala.util.Random(tokenY).nextInt()
    try {
      //tokens.get(Some(tokenName(randomtokenX) + randomtokenY.toString)))
      Some(tokenName(randomtokenX) + randomtokenY.toString)
    } catch {
      case ex: NullPointerException => {
        None
      }
    }
  }

  object LocalSlaService extends SlaService {
    def getSlaByToken(token: String): Future[Sla] = {
      val sla = /*localTokens.*/tokens.get(Some(token))
      Future {
        sla match {
          case None => throw new RuntimeException(s"Illegal token $token")
          case _ => sla.get
        }
      }
    }
  }
  //=====================================================
  def isRequestAllowed(token: Option[String]): Unit = {
    val service = new ThrottlingServiceImp(LocalSlaService)
    var res: Boolean = false

    res = service.isRequestAllowed(token)

    if (res)
      cntSuccessfulReq +=1
    else cntCancelledReq +=1
  }

  def getInto(token: Option[String]): SystemInfo = {
    Thread.sleep(5)
    //println(token)
    if (token.isDefined) {
      isRequestAllowed(token)
    }
    getSystemInfo
  }
  //=====================================================
}
//***********************************************************
object ThrottlingServiceImpStub{
  val CNTSuccessfulReq = "cntSuccessfulReq"
  val CNTCancelledReq = "cntCancelledReq"
  val emptySystemInfo =
    SystemInfo(Map(
      CNTSuccessfulReq -> -1.toString,
      CNTCancelledReq -> -1.toString))
  val startSystemInfo = //SystemInfo(MainSystemInfo(0, 0))
    SystemInfo(Map(
      CNTSuccessfulReq -> 0.toString,
      CNTCancelledReq -> 0.toString))
  val tokenX: Int = 2
  val tokenY: Int = 2
  val tokenName: Array[String] = Array("good", "bad")
  var tokens: Map[Option[String], Sla] = Map[Option[String], Sla]()
  //=================================================
  //CACHE
  //var lastReqTime: LocalDateTime
  var cntSuccessfulReq: Int = 0
  var cntCancelledReq: Int = 0

  def getSystemInfo: SystemInfo = {
    SystemInfo(Map(
      CNTSuccessfulReq -> cntSuccessfulReq.toString,
      CNTCancelledReq -> cntCancelledReq.toString))
  }

  def copyWithChage(mapStart: Map[String, String], k: String, v: String): SystemInfo = {
    SystemInfo(mapStart.filter((s: (String, String)) => s._1 != k) + (k -> v))
  }
}
