import AssemblyKeys._
import sbtassembly.Plugin._

import sbt._
import org.scalatra.sbt._
import org.scalatra.sbt.PluginKeys._
import com.earldouglas.xwp.JettyPlugin
import com.mojolly.scalate.ScalatePlugin._
import ScalateKeys._
import com.typesafe.sbt.packager.archetypes.JavaAppPackaging

/*
 * Dependencies
 */
val parserComb = "org.scala-lang.modules" %% "scala-parser-combinators" % "1.0.4"
val mongoDBDriverDep = "org.mongodb" %% "casbah" % "3.1.1"
val sparkCoreDep = "org.apache.spark" %% "spark-core" % "1.3.0" % "provided"
val sparkSQLDep = "org.apache.spark" %% "spark-sql" % "1.3.0" % "provided"
val sparkDataBricksDep = "com.databricks" % "spark-xml_2.10" % "0.3.3"
val unbescaped = "org.unbescape" % "unbescape" % "1.1.3.RELEASE"
val commonsCodec = "commons-codec" % "commons-codec" % "1.9"
val jobserver = "spark.jobserver" %% "job-server-api" % "0.6.2" % "provided"
val config = "com.typesafe" % "config" % "1.3.0"
val hadoopClient = ("org.apache.hadoop" % "hadoop-client" % "2.2.0")
  .exclude("commons-logging", "commons-logging")
  .exclude("commons-beanutils", "commons-beanutils-core")
  .exclude("commons-collections", "commons-collections")
val mongoDBHadoopCore = ("org.mongodb.mongo-hadoop" % "mongo-hadoop-core" % "1.5.1")
  .exclude("commons-logging", "commons-logging")
  .exclude("commons-beanutils", "commons-beanutils-core")
  .exclude("commons-collections", "commons-collections")


/*
 * Test-Dependencies
 */
val testDependencies = Seq(
  "org.slf4j" % "slf4j-simple" % "1.7.21" % "test",
  "junit" % "junit" % "4.11" % "test",
  "org.scalatest" % "scalatest_2.10" % "2.2.6" % "test"
)

/*
 * Settings
 */
lazy val commonSettings = Seq(
  organization := "HTW Berlin",
  name := "WikiPlag",
  version := "0.0.1",
  scalaVersion := "2.10.4",
  libraryDependencies ++= testDependencies
)

/*
 * Modules
 */
lazy val mongodb = (project in file("mongodb"))
  .settings(commonSettings: _*)
  .settings(
    name := "MongoDBConnection",
    libraryDependencies ++= Seq(
      mongoDBDriverDep
    )
  )

lazy val forwardreferencetable = (project in file("forwardreferencetable"))
  .settings(commonSettings: _*)
  .settings(
    name := "forwardreferencetable",
    libraryDependencies ++= Seq(
      commonsCodec
    )
  )

lazy val viewindex = (project in file("viewindex"))
  .settings(commonSettings: _*)
  .settings(
    name := "ViewIndex",
    libraryDependencies ++= Seq(
    )
  )

lazy val parser = (project in file("parser"))
  .settings(commonSettings: _*)
  .settings(
    name := "Parser",
    excludeFilter in unmanagedResources := "*",
    libraryDependencies ++= Seq(
      unbescaped
    )
  )
  
lazy val sparkApp = (project in file("sparkapp"))
  .settings(commonSettings: _*)
  .settings(
    name := "SparkApp",
    libraryDependencies ++= Seq(
      sparkCoreDep, sparkSQLDep, sparkDataBricksDep //, mongoDBHadoopCore, hadoopClient
    )
  ).settings(
    assemblySettings,
    jarName in assembly := "wikiplag_sparkapp.jar",
    assemblyOption in assembly := (assemblyOption in assembly).value.copy(includeScala = false)
  )
  .dependsOn(
    forwardreferencetable, viewindex, parser, mongodb
  )

lazy val analysisJob = (project in file("analysisJob"))
  .settings(commonSettings: _*)
  .settings(
    name := "AnalysisJob",
    libraryDependencies ++= Seq(
      sparkCoreDep, jobserver, config
    ),
    resolvers ++= Seq("Job Server Bintray" at "https://dl.bintray.com/spark-jobserver/maven"),
    assemblySettings,
    jarName in assembly := "analysisJob.jar"
  )
  .dependsOn(
    mongodb, forwardreferencetable, viewindex, parser
  )

lazy val similarity = (project in file("similarity"))
  .settings(commonSettings: _*)
  .settings(
    name := "Similarity",
    excludeFilter in unmanagedResources := "*",
    libraryDependencies ++= Seq(
    )
  )

lazy val stopwordfinder = (project in file("stopwordfinder"))
  .settings(commonSettings: _*)
  .settings(
    name := "stopwordfinder",
    excludeFilter in unmanagedResources := "*",
    libraryDependencies ++= Seq(
       sparkCoreDep, sparkSQLDep
    )
  )

val ScalatraVersion = "2.4.1"

lazy val webApp = (project in file("webapp"))
  .settings(ScalatraPlugin.scalatraSettings: _*)
  .settings(scalateSettings: _*)
  .settings(commonSettings: _*)
  .settings(
    name := "webapp",
    resolvers += Classpaths.typesafeReleases,
    resolvers += "Scalaz Bintray Repo" at "http://dl.bintray.com/scalaz/releases",
    libraryDependencies ++= Seq(
      "org.scalatra" %% "scalatra" % ScalatraVersion,
      "org.scalatra" %% "scalatra-scalate" % ScalatraVersion,
      "org.scalatra" %% "scalatra-specs2" % ScalatraVersion % "test",
      "ch.qos.logback" % "logback-classic" % "1.1.5" % "runtime",
      "org.eclipse.jetty" % "jetty-webapp" % "9.2.15.v20160210" % "compile;container",
      "javax.servlet" % "javax.servlet-api" % "3.1.0" % "container;provided",
      "org.scalatra" %% "scalatra-json" % ScalatraVersion,
      "org.json4s"   %% "json4s-jackson" % "3.3.0",
      "org.scalaj" %% "scalaj-http" % "2.3.0",
      "com.typesafe" % "config" % "1.3.0",
      "commons-codec" % "commons-codec" % "1.9"
    ),
    scalateTemplateConfig in Compile <<= (sourceDirectory in Compile){ base =>
      Seq(
        TemplateConfig(
          base / "webapp" / "WEB-INF" / "templates",
          Seq.empty,  /* default imports should be added here */
          Seq(
            Binding("context", "_root_.org.scalatra.scalate.ScalatraRenderContext", importMembers = true, isImplicit = true)
          ),  /* add extra bindings here */
          Some("templates")
        )
      )
    }
  )
  .dependsOn(
    mongodb,
    forwardreferencetable,
    viewindex,
    parser
  )
  .enablePlugins(JettyPlugin)
  .enablePlugins(JavaAppPackaging)

