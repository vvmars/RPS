package servises.accessory

import servises.Sla

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

object RpsServicesStub extends /*SlaService with */App{
  val tokenX: Int = 2
  val tokenY: Int = 2
  val tokenName: Array[String] = Array("good", "bad")

  class LocalTokens {
    var tokens: Map[Option[String], Sla] = Map[Option[String], Sla]()
    def set{
      for (i <- 1 to tokenX) {
        for (t <- tokenName) {
          tokens += (Some(t + i.toString) -> Sla(t + "_user" + i.toString, i * 2))
        }
      }
    }
  }

  val localTokens: LocalTokens = new LocalTokens
  localTokens.set
  //var randomtokenX = Random(tokenX)
  //var randomtokenY = Random(tokenY)

  implicit def getSlaByToken(token: String): Future[Sla] = {
    val sla = localTokens.tokens.get(Some(token))
    Future {
      sla match {
        case None => throw new RuntimeException(s"Illegal token $token")
        case _ => sla.get
      }
    }
  }
}
