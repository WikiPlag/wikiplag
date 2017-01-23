package de.htw.ai.wikiplag.backend

import com.typesafe.config.ConfigFactory
import de.htw.ai.wikiplag.data.MongoDbClient
import de.htw.ai.wikiplag.model.Document
import de.htw.ai.wikiplag.textProcessing.plagiarism.PlagiarismFinder
import de.htw.ai.wikiplag.textProcessing.plagiarism.PlagiarismFinder.{ID, InPos, WikPos}
import org.apache.spark.{SparkConf, SparkContext}
import org.json4s.{DefaultFormats, Formats}
import org.scalatra.json._

import scala.collection.mutable

class WikiplagWebServlet extends WikiplagWebAppStack with JacksonJsonSupport {
	protected implicit val jsonFormats: Formats = DefaultFormats

	private var sparkContext: SparkContext = _
	private var mongoClient: MongoDbClient = _
	private val documentCache = mutable.Map[Long, Document]()
	private val SlidingSize = 30

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
		println(s"post /wikiplag/analyse with '$inputText'")

		if (inputText != null && !inputText.isEmpty) {
			//			1. Triple Anfang des Plagiate
			//          2. Ende des Plagiate
			//          3. Score ...
			//
			//        Triple:
			//           1. Position im Input
			//           2. WikiId
			//			 3. Position im Artikel
			val result = new PlagiarismFinder().apply(sparkContext, inputText)
//			println(mongoClient.getInvIndex("informatik"))

			val documentTextBuilder = StringBuilder.newBuilder
			val plags = result
					.groupBy(x => x._1._1) // group by start pos
					.zipWithIndex // to map with index
					.map(x => {
						val wiki_excerpts = x._1._2.map(y => {
							val wikiId = y._1._2
							val startTextPos = y._1._3
							val endTextPos = y._2._3

							val document = documentCache.getOrElse(wikiId, {
								val doc = mongoClient.getDocument(wikiId)
								documentCache(wikiId) = doc
								doc
							})

							// build text with <span>
							documentTextBuilder.clear()
							documentTextBuilder.append("[...]")
							if ((startTextPos - SlidingSize) > 0) {
								documentTextBuilder.append(document.text.substring(startTextPos - SlidingSize, startTextPos))
							}
							documentTextBuilder.append("<span class=\"wiki_plag\">")
							documentTextBuilder.append(document.text.substring(startTextPos, endTextPos))
							documentTextBuilder.append("</span>")
							if ((endTextPos - SlidingSize) < document.text.length) {
								documentTextBuilder.append(document.text.substring(startTextPos - SlidingSize, startTextPos))
							}
							documentTextBuilder.append("[...]")

							Map(
								"title" -> document.title,
								"id" -> wikiId,
								"start" -> startTextPos,
								"end" -> endTextPos,
								"excerpt" -> documentTextBuilder.mkString
							)
						})
						Map(
							"id" -> x._2,
							"wiki_excerpts" -> wiki_excerpts
						)
					})

			// TODO: insert span into inputtext
			Map(
				"tagged_input_text" -> inputText,
				"plags" -> plags
			)
		} else {
			halt(400)
		}
	}

	// 1 Finder rufen
	// 2 title / text für id holen
	// 3 span tags einbauen (Inputtext und pro Treffer (excerpt))
	// überlappende start / ende positionen beachten
	// ~5 Wörter danach und vor dem Artikel [...]
	// profit

	//	Als im Sommer 1990 die deutsche Einheit bevorstand und Bundeskanzler Helmut Kohl nach dem Mantel der Geschichte griff, brach es aus seinem Vorgänger Helmut Schmidt heraus. "Mein Gott, was gäbe ich darum, daran noch mitwirken zu dürfen", schrieb er in einem Manuskript. Und strich die Passage vor der Veröffentlichung. Keiner sollte mitbekommen, dass auch der große Helmut Schmidt unter einem Problem litt, das viele Pensionäre kennen: nicht loslassen zu können. Schmidt hatte von 1953 an im Bundestag gesessen, später diverse Ministerposten innegehabt, 1974 war er für acht Jahre ins Kanzleramt eingezogen. Seine Karriere dauerte also rund ein Dritteljahrhundert - und begann doch danach erst richtig. Er wurde Elder Statesman, Orakel, Alleswisser, die Verehrung hätte größer nicht sein können. Über diese späten Jahre, in denen Schmidt ohne Amt und Würden auskommen musste, hat Thomas Karlauf eine Biografie geschrieben, die Schmidt-Fans ernüchtern könnte*. Entgegen der verbreiteten Annahme, der Altkanzler sei auch nach dem Sturz ein mächtiger Player geblieben, präsentiert Karlauf einen frustrierten Expolitiker, dessen Bücher zwar Millionen Käufer fanden, dessen Meinung aber ohne Resonanz blieb. "Von den Leuten in Berlin will kaum einer meine Ratschläge annehmen", klagte Schmidt 2003 in einem Brief. Der Film gilt als einer der schlechtesten Filme der 1990er Jahre und gewann fünf Goldene Himbeeren. Karlauf, 61, schildert manches aus eigener Anschauung. Seit 1987 ging der Lektor dem Altkanzler bei Memoiren und Politikbüchern zur Hand. Anderes kennt er aus Schmidts Archiv. Er habe, schreibt Karlauf, "schamlos alle Papiere herausgezogen, die sich später möglicherweise in irgendeinem Zusammenhang als nützlich erweisen" könnten. Zudem hat er Interviews geführt, unter anderen mit Altkanzler Gerhard Schröder. Der Biograf rechnet der Einflussnahme Schmidts ganze zwei Entscheidungen während Schröders sieben Jahre dauernder rot-grüner Koalition zu. Schmidt half dabei, einen deutschen Kandidaten für den Direktorenposten des Internationalen Währungsfonds zu finden und die Kunstsammlung des Sammlers Heinz Berggruen

}
