package de.htw.ai.wikiplag.backend.models

import com.mongodb.casbah.Imports._
import de.htw.ai.wikiplag.backend.helpers.InputAdaption
import de.htw.ai.wikiplag.connection.MongoDBImpl
import de.htw.ai.wikiplag.forwardreferencetable.ForwardReferenceTableImp
import de.htw.ai.wikiplag.parser.WikiDumpParser
import de.htw.ai.wikiplag.viewindex.ViewIndexBuilderImp

import scala.collection.mutable.ListBuffer

class InitInput(text: String, step: String) {
	def handle() = {
		val tokens = WikiDumpParser.extractWords(text, ListBuffer(), 0).map(_.toLowerCase())
		val table = ForwardReferenceTableImp.buildForwardReferenceTable(tokens, step.toInt)
		val originViewIndex = ViewIndexBuilderImp.buildViewIndex(text.toLowerCase, tokens)

		MongoDBImpl.open(
			new ServerAddress("hadoop03.f4.htw-berlin.de", 27020),
			MongoCredential.createCredential("REPLACE-ME", "REPLACE-ME", "REPLACE-ME".toCharArray)
		)
		// List[(Hash, List of (document_id, position))]
		val siml = MongoDBImpl.findSimilarity(table.keys.toList, step.toInt)
		// List[(Hashes, List[(Doc_id, List[Positions])])]
		val fitSim = siml.map(hashSim =>
			(hashSim._1, hashSim._2.groupBy(_._1).mapValues(list => list.map(x => x._2)).toList)
		)
		// model : (Hash, List(DocId, Position), List(originPosition))
		val injectedWithOriginPosition = InputAdaption.injectPosition(fitSim, table.toMap)
		val replacedWithCharPosition = InputAdaption.replaceListOfWordPositionsWithCharPosition(
			injectedWithOriginPosition, originViewIndex, step.toInt
		)
		val groupedMatches = InputAdaption.groupHashesByDocs(replacedWithCharPosition)

		// extract docIds
		val docIds = InputAdaption.extractDocIds(fitSim)
		val titles = MongoDBImpl.getTitle(docIds)
		val wikiTexts = MongoDBImpl.getOriginalText(docIds)
		val viewIndex = MongoDBImpl.getViewIndex(docIds)
		val injectedWithWiki = InputAdaption.injectWikiPositions(groupedMatches, viewIndex, step.toInt)
		val withSimValue = InputAdaption.injectSimilarityValue(injectedWithWiki, table.size)
		val filtered = InputAdaption.filterHashMap(withSimValue, 0.1)

		MongoDBImpl.close()
		(InputAdaption.injectTitleAndText(filtered, titles, wikiTexts), table.size)
	}

	def getResult() = {
		val res = this.handle()
		prepare(res._1, res._2)
	}

	def addKeys(response: Map[Long, (List[(String, List[Int], List[Int])], Double)])
	: Map[Long, (List[((String, String), (String, List[Int]), (String, List[Int]))], (String, Double))] = {

		val hashKey = "key"
		val wikiCharIndexKey = "wikiCharPosition"
		val originCharKey = "originCharPosition"
		val similarityValueKey = "similarityValue"

		response.mapValues(value => {
			(value._1.map(hashAndPositions => {
				((hashKey, hashAndPositions._1), (wikiCharIndexKey, hashAndPositions._2), (originCharKey, hashAndPositions._3))
			}), (similarityValueKey, value._2))
		})
	}


	def prepare(response: Map[Long, (List[(String, List[CharPosition], List[CharPosition])], Double, String, String)],
	            hashSize: Int): ResponseInitInput = {
		ResponseInitInput(response.map(value => {
			SuspiciousDocs(
				value._1,
				value._2._1.map(v => HashKeyAndPositions(v._1, v._2, v._3)),
				value._2._2,
				value._2._3,
				value._2._4
			)
		}).toList, hashSize)
	}

}
