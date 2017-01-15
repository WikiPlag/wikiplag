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
	scalaVersion := "2.10.4",
	libraryDependencies ++= testDependencies
)

unmanagedBase <<= baseDirectory { base => base / "libs" }

assemblyMergeStrategy in assembly := {
	case PathList("META-INF", xs@_*) => MergeStrategy.discard
	case x => MergeStrategy.first
}

lazy val root = (project in file("."))
		.settings(ScalatraPlugin.scalatraSettings: _*)
		.settings(scalateSettings: _*)
		.settings(commonSettings: _*)
		.settings(
			resolvers += Classpaths.typesafeReleases,
			resolvers += "Scalaz Bintray Repo" at "http://dl.bintray.com/scalaz/releases",
			resolvers += "jitpack" at "https://jitpack.io",
			libraryDependencies ++= Seq(
				"org.mongodb" %% "casbah" % "3.1.1",
				"org.scalatra" %% "scalatra" % ScalatraVersion,
				"org.scalatra" %% "scalatra-scalate" % ScalatraVersion,
				"org.scalatra" %% "scalatra-specs2" % ScalatraVersion % "test",
				"ch.qos.logback" % "logback-classic" % "1.1.5" % "runtime",
				"org.eclipse.jetty" % "jetty-webapp" % "9.2.15.v20160210" % "compile;container",
				"javax.servlet" % "javax.servlet-api" % "3.1.0" % "container;provided",
				"org.scalatra" %% "scalatra-json" % ScalatraVersion,
				"org.json4s" %% "json4s-jackson" % "3.3.0",
				"org.scalaj" %% "scalaj-http" % "2.3.0",
				"com.typesafe" % "config" % "1.2.1",
				"commons-codec" % "commons-codec" % "1.9" % "provided",
//				"org.apache.spark" %% "spark-core" % "1.5.0" % "provided"
				("org.apache.spark" %% "spark-core" % "1.5.0")
						.exclude("org.eclipse.jetty.orbit", "javax.servlet")
						.exclude("org.eclipse.jetty.orbit", "javax.transaction")
						.exclude("org.eclipse.jetty.orbit", "javax.mail")
						.exclude("org.eclipse.jetty.orbit", "javax.activation")
						.exclude("commons-beanutils", "commons-beanutils-core")
						.exclude("commons-collections", "commons-collections")
						.exclude("com.esotericsoftware.minlog", "minlog")
//				// 'sbt package' with these project and place them into at /libs
				//				"com.github.WikiPlag" % "analyzer" % "-SNAPSHOT",
				//				"com.github.WikiPlag" % "wikiplag_utils" % "-SNAPSHOT"
			),
			// https://github.com/FasterXML/jackson-module-scala/issues/214
			dependencyOverrides += "com.fasterxml.jackson.module" % "jackson-module-scala_2.10" % "2.4.4",
			dependencyOverrides += "com.fasterxml.jackson.core" % "jackson-databind" % "2.4.4",
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
