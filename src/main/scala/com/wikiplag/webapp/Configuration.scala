package com.wikiplag.webapp
import com.typesafe.config.ConfigFactory

object Configuration {
  private val path = "application.conf"
  private val config = ConfigFactory.load(path)
  val url = config.getConfig("InitInput").getString("url")
  val prefix = config.getConfig("InitInput").getString("prefix")
}
