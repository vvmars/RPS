package servises.accessory

import config.Configuration
import design.LocalCache.userRequests
import design.SystemInfo
import servises.{Sla, SlaService, ThrottlingServiceImp}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.collection.JavaConversions._

class ThrottlingServiceImpStub
extends Configuration{

  import ThrottlingServiceImpStub._

  set(getRps)
  //println(tokens)

  def set (rps: Int = 1): Unit = {
    tokens = Map[Option[String], Sla]()
    for (i <- 0 to tokenX) {
      for (t <- tokenName) {
        tokens += (Some(t + i.toString) -> Sla(t + "_user" + i.toString, rps))
      }
    }
  }

  def getToken: Option[String] = {
    val randomtokenX: Int = (Math.random() * tokenName.length).toInt
    val randomtokenY: Int = (Math.random() * tokenY).toInt
    try {
      //println("x - " + randomtokenX + ", y - " + randomtokenY)
      //println(tokens)
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


  def getSystemInfoByUserRequests:SystemInfo = {
    var cntSuccessfulReq: Int = 0
    var cntCancelledReq: Int = 0
    userRequests.keys().toList.foreach((key:Option[String]) => {
      val v = userRequests.get(key)
      cntSuccessfulReq += v.cntSuccessfulReq
      cntCancelledReq += v.cntCancelledReq
    })
    SystemInfo(Map(
      CNTSuccessfulReq -> cntSuccessfulReq.toString,
      CNTCancelledReq -> cntCancelledReq.toString))
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

  def getInto(token: Option[String], waitMilles: Int): SystemInfo = {
    Thread.sleep(waitMilles)
    //println(token)
    if (token.isDefined) {
      isRequestAllowed(token)
    }
    getSystemInfo
  }

  def getInto(token: Option[String]): SystemInfo = {
    getInto(token, 0)
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
  val tokenX: Int = 2 //including "0"
  val tokenY: Int = 2
  val tokenName: Array[String] = Array("good", "bad", "middle", "perfect")
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
