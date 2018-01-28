package config

import com.typesafe.config.ConfigFactory
import design.Constant

import scala.util.Try

/**
  * To read app configuration
  */
trait Configuration {
  /**
    * Application config object.
    * Read the configuration from the application.conf file
    */
  private val config = ConfigFactory.load()

  /**
    * All unauthorized user's requests are limited by GraceRps
    */
  def getGraceRps: Int = Try(config.getInt("RPS.default.graceRps")).getOrElse(Constant.DefGraceRps)
  def getRps: Int = Try(config.getInt("RPS.default.rps")).getOrElse(Constant.DefRps)
  def getEnv: String = Try(config.getString("RPS.default.env")).getOrElse(Constant.DefEnv)
}
