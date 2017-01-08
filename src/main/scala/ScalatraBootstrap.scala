import javax.servlet.ServletContext

import de.htw.ai.wikiplag.backend.WikiplagWebServlet
import org.scalatra._

class ScalatraBootstrap extends LifeCycle {

	override def init(context: ServletContext) {
		// Mount servlets.
		context.mount(new WikiplagWebServlet, "/*")
	}
}
