package de.htw.ai.wikiplag.analysisJob
import com.typesafe.config.{Config, ConfigFactory}
import org.apache.spark._
import scala.util.Try
import spark.jobserver._

object TextInputJob extends SparkJob {
  def main(args: Array[String]) {
    val conf = new SparkConf().setMaster("local[4]").setAppName("TextInputJob")
    val sc = new SparkContext(conf)
    val config = ConfigFactory.parseString("")
    val results = runJob(sc, config)
    println("Result is " + results)
  }

  override def validate(sc: SparkContext, config: Config): SparkJobValidation = {

    Try(config.getString("text") ++ config.getString("step"))
      .map(x => SparkJobValid)
      .getOrElse(SparkJobInvalid("text and step config params should be defined"))
  }

  override def runJob(sc: SparkContext, config: Config): Any = {
//    sc.parallelize(config.getString("text").split(" ").toSeq).countByValue
//    List(config.getString("text"), config.getString("step"))
    InputJobHandler.handleJob(config.getString("text"), config.getString("step").toInt).toString
  }
}
