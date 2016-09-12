package main

import java.io.{BufferedWriter,OutputStreamWriter, FileOutputStream}

import org.apache.spark.rdd.RDD
import org.apache.spark.{SparkConf, SparkContext}

object stopWordFinderApp extends App {
  val stopWordsPath = "stopwords.txt"
  val exampleTextPath= "example.txt"
  val conf:SparkConf = new SparkConf().setMaster("local[4]").setAppName("stopWordFinder")
  val sc:SparkContext = new SparkContext(conf)
  val stopWords = getStopWords(stopWordsPath)
  val exampleText = getExampleRDD(exampleTextPath)

  def getStopWords(fileName:String): List[String] = {
    val url= getClass.getResource("/"+fileName).getPath
    val io = scala.io.Source.fromFile(url, "UTF-8")
    val result= io.getLines().foldLeft(List():List[String])((list,el)=> el::list)
    io.close()
    result
  }

  def getExampleRDD(fileName:String): RDD[String] = {
    val url = getClass.getResource("/"+fileName).getPath
    sc.textFile(url).map(line => line.split(" ")).flatMap(x=>x)
  }
  val top50 = stopWordFinder.findStopWords(exampleText,stopWords, 50)

  val writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("top50.txt"), "UTF-8"))
  top50.foreach(word=> writer.write(word + "\n"))
  writer.close()

}


object stopWordFinder {
  def findStopWords(text:RDD[String], stopw:List[String], amount:Int): List[String] = {
    text.filter(word => stopw.contains(word))
      .map(word => (word, 1))
      .reduceByKey(_ + _)
      .map(_.swap)
      .sortByKey(false, 1)
      .map(_._2)
      .take(amount)
      .toList
  }
}