package de.htw.ai.wikiplag.backend

import com.typesafe.config.ConfigFactory
import de.htw.ai.wikiplag.data.MongoDbClient
import org.apache.spark.{SparkConf, SparkContext}
import org.json4s.{DefaultFormats, Formats}
import org.scalatra.json._
import org.scalatra.scalate.ScalateSupport

class WikiplagWebServlet extends WikiplagWebAppStack with ScalateSupport with JacksonJsonSupport {
	protected implicit val jsonFormats: Formats = DefaultFormats

	private var sparkContext: SparkContext = _
	private var mongoClient: MongoDbClient = _

	override def init(): Unit = {
		val config = ConfigFactory.load("backend.properties")

		val sparkMaster = config.getString("spark.master")
		val scConfig = new SparkConf()
		sparkContext = new SparkContext(sparkMaster, "WikiplagBackend", scConfig)

		val host = config.getString("database.host")
		val port = config.getInt("database.port")
		val username = config.getString("database.user")
		val database = config.getString("database.database")
		val password = config.getString("database.password")

		mongoClient = MongoDbClient(sparkContext, host, port, username, database, password)
	}

	// Before every action runs, set the content type to be in JSON format.
	before() {
		contentType = formats("json")
	}

	/*
	 * document path
	 */

	get("/wikiplag/document/:id") {
		val wikiId = params("id").asInstanceOf[Long]
		val document = mongoClient.getDocument(wikiId)
		document
	}

//	{
//		"doc_id": 1337,
//		"title": "title",
//		"text": "text..."
//	}

	/*
	 * plagiarism path
	 */

	post("/wikiplag/analyse") {
		contentType = formats("json")

		val text = params("text")
		val x = new PlagiarismFinder().apply(sparkContext, text)
		x
	}

//	{
//		"hits": [
//		{
//			"title": "title",
//			"doc_id": 1337,
//			"score": 100,
//			"preview": "word <em>text1</em> word 2",
//			"origin": "text <em>text1</em> text3 text2 text1"
//		},
//		{
//			"title": "title 2",
//			"doc_id": 1338,
//			"score": 100,
//			"preview": "word <em>text1</em> word 2",
//			"origin": "text <em>text1</em> text3 text2 text1"
//		},
//		{
//			"title": "title 3",
//			"doc_id": 1339,
//			"score": 100,
//			"preview": "word <em>text1</em> word 2",
//			"origin": "text <em>text1</em> text3 text2 text1"
//		}
//		]
//	}

}
