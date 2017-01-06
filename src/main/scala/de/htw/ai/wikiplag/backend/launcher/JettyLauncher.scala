package de.htw.ai.wikiplag.backend.launcher

import org.eclipse.jetty.server.Server
import org.eclipse.jetty.servlet.DefaultServlet
import org.eclipse.jetty.webapp.WebAppContext
import org.scalatra.servlet.ScalatraListener

object JettyLauncher {

	def main(args: Array[String]) {
		//    val port = if(System.getProperty("http.port") != null) System.getProperty("http.port").toInt else 8080
		val port = if (System.getenv("PORT") != null) System.getenv("PORT").toInt else 8080
		val server = new Server(port)
		val context = new WebAppContext()
		context.setContextPath("/")
		context.setResourceBase("src/main/webapp")

		context.setEventListeners(Array(new ScalatraListener))
		context.addServlet(classOf[DefaultServlet], "/")

		server.setHandler(context)

		server.start()
		server.join()
	}
}