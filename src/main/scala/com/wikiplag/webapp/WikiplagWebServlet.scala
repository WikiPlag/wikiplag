package com.wikiplag.webapp

import java.io.File

import com.wikiplag.webapp.models.InitInput
import org.scalatra._
import scalate.ScalateSupport
import org.json4s.{DefaultFormats, Formats}
import org.scalatra.json._

class WikiplagWebServlet extends WikiplagWebAppStack with ScalateSupport with JacksonJsonSupport{
  protected implicit val jsonFormats: Formats = DefaultFormats

  get("/index.html") {
    contentType="text/html"
    new File(servletContext.getResource("/index.html").getFile)
  }
  get("/") {
    contentType="text/html"
    new File(servletContext.getResource("/index.html").getFile)
  }
  post("/handleInput") {
    contentType = formats("json")
    new InitInput(params("text"), params("step")).getResult()
  }
  post("/calculate") {
    contentType = formats("json")

  }

}
