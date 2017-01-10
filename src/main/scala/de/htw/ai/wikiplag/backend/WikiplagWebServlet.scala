package de.htw.ai.wikiplag.backend

import com.typesafe.config.ConfigFactory
import de.htw.ai.wikiplag.data.{InverseIndexBuilderImpl, MongoDbClient}
import de.htw.ai.wikiplag.textProcessing.plagiarism.PlagiarismFinder
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

		val conf = new SparkConf()
				.setMaster(config.getString("spark.master"))
				.setAppName("WikiplagBackend")
				.set("spark.executor.memory", "4g")
				.set("spark.storage.memoryFraction", "0.8")
				.set("spark.driver.memory", "2g")

		sparkContext = new SparkContext(conf)

		val host = config.getString("mongo.host")
		val port = config.getInt("mongo.port")
		val username = config.getString("mongo.user")
		val database = config.getString("mongo.database")
		val password = config.getString("mongo.password")

		mongoClient = MongoDbClient(sparkContext, host, port, username, database, password)
	}

	// Before every action runs, set the content type to be in JSON format.
	before() {
		contentType = formats("json")
	}

	/*
	 * Test Path
	 */
	get("/") {
		println("get /")
		"Hallo Wikiplag REST Service"
	}

	/*
	 * document path
	 */

	get("/wikiplag/document/:id") {
		val wikiId = params("id").toLong
		println(s"get /wikiplag/document/:id with $wikiId")

		val document = mongoClient.getDocument(wikiId)
		if (document != null) {
			document
		} else {
			halt(404)
		}

	}

	/*
	 * plagiarism path
	 */

	post("/wikiplag/analyse") {
		println("post /wikiplag/analyse")
		val inputText = params.getOrElse("text", halt(400))
		println(s"post /wikiplag/analyse with $inputText")

		if (inputText != null && !inputText.isEmpty) {
			// TODO: how to use PlagiarismFinder
			val keySet = InverseIndexBuilderImpl.buildIndexKeySet(inputText)
			val index = mongoClient.getInvIndexRDD(keySet)
			val result = new PlagiarismFinder().apply(sparkContext, inputText)

			Map("hits" -> result)
		} else {
			halt(400)
		}
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
