package config

import com.typesafe.config.ConfigFactory
import scala.util.Try

/**
  * To read app configuration
  */
trait Configuration {
  /**
    * Application config object.
    * Read the configuration from the application.conf file
    */
  private val DefGraceRps: Int = 10
  private val DefRps: Int = 2
  private val config = ConfigFactory.load()

  /**
    * All unauthorized user's requests are limited by GraceRps
    */
  def getGraceRps: Int = Try(config.getInt("RPS.default.graceRps")).getOrElse(DefGraceRps)
  def getRps: Int = Try(config.getInt("RPS.default.rps")).getOrElse(DefRps)
}
