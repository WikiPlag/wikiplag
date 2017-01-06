import com.earldouglas.xwp.JettyPlugin
import com.mojolly.scalate.ScalatePlugin.ScalateKeys._
import com.mojolly.scalate.ScalatePlugin._
import com.typesafe.sbt.packager.archetypes.JavaAppPackaging
import org.scalatra.sbt._
import sbt.Keys._
import sbt._

/*
 * Dependencies
 */
val wikiplag = "com.github.WikiPlag" % "wikiplag_utils" % "-SNAPSHOT"
val analyzer = "com.github.WikiPlag" % "analyzer" % "algoonly-SNAPSHOT"
val testDependencies = Seq(
	"org.slf4j" % "slf4j-simple" % "1.7.21" % "test",
	"junit" % "junit" % "4.11" % "test",
	"org.scalatest" % "scalatest_2.10" % "2.2.6" % "test"
)

/*
 * Properties
 */
val ScalatraVersion = "2.4.1"

/*
 * Settings
 */
lazy val commonSettings = Seq(
	organization := "HTW Berlin",
	name := "WikiPlag",
	version := "0.0.1",
	scalacOptions ++= Seq("-encoding", "UTF-8"),
	scalaVersion := "2.10.6",
	libraryDependencies ++= testDependencies
)

lazy val root = (project in file("."))
		.settings(ScalatraPlugin.scalatraSettings: _*)
		.settings(scalateSettings: _*)
		.settings(commonSettings: _*)
		.settings(
			resolvers += Classpaths.typesafeReleases,
			resolvers += "Scalaz Bintray Repo" at "http://dl.bintray.com/scalaz/releases",
			resolvers += "jitpack" at "https://jitpack.io",
			libraryDependencies ++= Seq(
				"org.scalatra" %% "scalatra" % ScalatraVersion,
				"org.scalatra" %% "scalatra-scalate" % ScalatraVersion,
				"org.scalatra" %% "scalatra-specs2" % ScalatraVersion % "test",
				"ch.qos.logback" % "logback-classic" % "1.1.5" % "runtime",
				"org.eclipse.jetty" % "jetty-webapp" % "9.2.15.v20160210" % "compile;container",
				"javax.servlet" % "javax.servlet-api" % "3.1.0" % "container;provided",
				"org.scalatra" %% "scalatra-json" % ScalatraVersion,
				"org.json4s" %% "json4s-jackson" % "3.3.0",
				"org.scalaj" %% "scalaj-http" % "2.3.0",
				"com.typesafe" % "config" % "1.3.0",
				"commons-codec" % "commons-codec" % "1.9",
				wikiplag,
				analyzer
			),
			scalateTemplateConfig in Compile <<= (sourceDirectory in Compile) { base =>
				Seq(
					TemplateConfig(
						base / "webapp" / "WEB-INF" / "templates",
						Seq.empty, /* default imports should be added here */
						Seq(
							Binding("context", "_root_.org.scalatra.scalate.ScalatraRenderContext", importMembers = true, isImplicit = true)
						), /* add extra bindings here */
						Some("templates")
					)
				)
			}
		)
		.enablePlugins(JettyPlugin)
		.enablePlugins(JavaAppPackaging)

