package servises.accessory

import java.util.concurrent.{ExecutorService, Executors, TimeUnit}
import com.typesafe.scalalogging.Logger
import config.Configuration
import design.{LocalCache, SystemInfo}
import org.slf4j.LoggerFactory

class ThreadPoolService
  extends Configuration {

  private val logger = Logger(LoggerFactory.getLogger(this.getClass))

  def submitThrottlingRequest(cntUsers: Int, cntRps: Int, durationSec: Int,
                              throttlingServiceImp: ThrottlingServiceImpStub
         ): Unit = {
    //val throttlingServiceImp = new ThrottlingServiceImpStub
    val executor: ExecutorService = Executors.newFixedThreadPool(cntUsers)
    val waitMSec = durationSec * 1000 / cntRps
    try {
      for (i <- 1 to cntUsers * cntRps) {
        val token = throttlingServiceImp.getToken
        executor.submit(new Handler(token, waitMSec, throttlingServiceImp.getInto _))
      }

      //      IntStream.range(0, cntRps).forEach(
      //      (i: Int) => executor.submit(Handler(token, waitMSec, getInto)))
    } finally {
      executor.shutdown()
      executor.awaitTermination(2 * durationSec, TimeUnit.SECONDS)
      if (!executor.isTerminated()) {
        logger.info("The executor has not finished yet and it'l be finished immediate")
      }
      val rejectedSize = executor.shutdownNow()
      logger.info(s"The executor is shutdown. /rejected - $rejectedSize/")
    }
  }
}

class Handler(token: Option[String],
              waitMSec: Int,
              getInto:(Option[String], Int) => SystemInfo)
  extends Runnable
  with Configuration {
  private val threadName = Thread.currentThread.getName
  private val logger = Logger(LoggerFactory.getLogger(this.getClass))
  //val throttlingServiceImpStub = new ThrottlingServiceImpStub()
  def run(): Unit = {
    logger.debug("Start running RPS service in {}", threadName)
    val systemInfo = getInto(token, waitMSec)
    Unit
  }
}

object eee extends App{
  val throttlingServiceImp = new ThrottlingServiceImpStub
  val dd = new ThreadPoolService
  dd.submitThrottlingRequest(1,4,1, throttlingServiceImp)
/*  println("-----------")
  println(LocalCache.userRequests)
  println("-----------")
  println(LocalCache.users)*/

  //system.shutdown(system)
}
