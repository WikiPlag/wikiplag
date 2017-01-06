package de.htw.ai.wikiplag.backend.helpers

import de.htw.ai.wikiplag.backend.models.CharPosition

import scala.util.Try

object InputAdaption {
	/**
	  * ngMatches  - List[(Hashes, List[(Doc_id, List[Positions])])]
	  * threshold basically is used for filtering end results.
	  * if the ratio of size to occurrences in one doc id less than threshold, this doc id will be threw away
	  *
	  * @param ngMatches  only matches hash, that we receive from DB List[(,List[()])]
	  * @param originSize means, number of all hashes of origin document
	  * @param threshold  for filtering results
	  * @return List[(Hashes, List[(FilteredDoc_id, List[Positions])], )]
	  */
	def generateWhitelist(ngMatches: List[(String, List[(Long, List[Int])])], originSize: Int, threshold: Double)
	: List[(String, List[(Long, List[Int])], List[(Long, Double)])] = {
		val filteredNg = filterMatches(ngMatches, originSize, threshold)
		filteredNg.map(item => (
				item._1, item._2, item._2.map(
			doc => (doc._1, (item._2.size / originSize).toDouble)
		)
		)
		)
	}

	def filterMatches(ngMatches: List[(String, List[(Long, List[Int])])], size: Int, threshold: Double):
	List[(String, List[(Long, List[Int])])] = {
		ngMatches.filter(item => (item._2.size / size) > threshold)
	}

	/**
	  * List((hash1,List((Docid1,List(1, 2, 3, 4)), (DocId2,List(4, 5, 6)))), (hash2,List((Docid1,List(1, 2, 3, 4)), (DocId2,List(4, 5, 6)))))
	  * will give List(Docid1, Docid2)
	  * Will parse ngMatches and look for id
	  *
	  * @param ngMatches
	  * @return List of Ids
	  */
	def extractDocIds(ngMatches: List[(String, List[(Long, List[Int])])]): List[Long] = {
		ngMatches.map(_._2).flatten.groupBy(_._1).keys.toList
	}

	/**
	  * take a list of hashes, i.e. List[(Hash, List[(DocId, List[Position])])]
	  * and will group it by docs, instead by hashes.
	  *
	  * @param hashItems List[(Hash, List[(DocId, List[Position])])]
	  * @return List[(DocId, List[(Hash, List[Position])])]
	  */
	def groupHashesByDocs(hashItems: List[(String, List[CharPosition], List[(Long, List[Int])])])
	: Map[Long, List[(String, List[Int], List[CharPosition])]] = {
		hashItems
				.flatMap(hash => hash._3.map(docIdAndPositions => expandTuple(docIdAndPositions, hash._1, hash._2)))
				.groupBy(_._1)
				.mapValues(x => x.map(y => (y._2, y._3, y._4)))
	}

	def expandTuple(docIdAndPositions: (Long, List[Int]), hash: String, charIndexes: List[CharPosition])
	: (Long, String, List[Int], List[CharPosition]) = {
		val docId = docIdAndPositions._1
		val positions = docIdAndPositions._2
		(docId, hash, positions, charIndexes)
	}

	/**
	  * related to origin article
	  *
	  * @param hashList List[(hash, list[or])]
	  * @param viewIndex
	  * @return
	  */
	def replaceListOfWordPositionsWithCharPosition(hashList: List[(String, List[Int], List[(Long, List[Int])])],
	                                               viewIndex: List[(Int, Int, Int)], step: Int)
	: List[(String, List[CharPosition], List[(Long, List[Int])])] = {
		val groupedView = viewIndex.groupBy(_._1).mapValues(_.head)
		hashList.map(hashAndPositions => replaceMapValueWithCharPosition(hashAndPositions, groupedView, step))
	}

	private def replaceMapValueWithCharPosition(hashAndPositions: (String, List[Int], List[(Long, List[Int])]),
	                                            viewIndex: Map[Int, (Int, Int, Int)], step: Int)
	: (String, List[CharPosition], List[(Long, List[(Int)])]) = {
		val replaced = hashAndPositions._2.map(wordPosition => {
			val endWordPosition = wordPosition + step - 1
			val endOriginChar = Try(viewIndex.get(endWordPosition))
			val startOriginChar = Try(viewIndex.get(wordPosition))
			if (startOriginChar.isFailure) print(startOriginChar.failed.get)
			if (endOriginChar.isFailure) print(endOriginChar.failed.get)
			val endCharIndex = endOriginChar.get.get._2 + endOriginChar.get.get._3
			CharPosition(startOriginChar.get.get._2, endCharIndex)
		})
		(hashAndPositions._1, replaced, hashAndPositions._3)
	}

	/**
	  * extend map to have a positions(List) of wikiArticle
	  *
	  * @param hashMap
	  * @param viewIndex
	  * @return
	  */
	def injectWikiPositions(hashMap: Map[Long, List[(String, List[Int], List[CharPosition])]],
	                        viewIndex: Map[Long, List[(Int, Int, Int)]],
	                        step: Int)
	: Map[Long, List[(String, List[CharPosition], List[CharPosition])]] = {

		val groupedIndex = groupIndexValueByPosition(viewIndex)
		hashMap.map(hash => {
			val docId = hash._1
			val indexesWiki = groupedIndex.get(docId)
			hash._1 -> hash._2.map(hashAndPositions => {
				val hash = hashAndPositions._1
				val positionsWiki = hashAndPositions._2
				val charIndexOrigin = hashAndPositions._3

				val startCharIndexesWiki = positionsWiki.flatMap(x => indexesWiki.get(x).map(_._2))
				val endCharIndexesWiki = positionsWiki.flatMap(position =>
					indexesWiki.get(position + step - 1).map(viewIndex => viewIndex._2 + viewIndex._3)
				)
				val charPosition = startCharIndexesWiki.zip(endCharIndexesWiki).map(pos => CharPosition(pos._1, pos._2))
				(hash, charPosition, charIndexOrigin)
			})
		}
		)
	}

	def groupIndexValueByPosition(index: Map[Long, List[(Int, Int, Int)]])
	: Map[Long, Map[Int, List[(Int, Int, Int)]]] = {
		index.mapValues(viewIndex => viewIndex.groupBy(_._1))
	}


	/**
	  * will inject into a map a value
	  * which corresponds to ratio
	  * of all hashes in original text
	  * and hashes that were found in wiki article
	  *
	  * @param hashMap
	  * @param numberOfHashes
	  * @return
	  */
	def injectSimilarityValue(hashMap: Map[Long, List[(String, List[CharPosition], List[CharPosition])]], numberOfHashes: Int)
	: Map[Long, (List[(String, List[CharPosition], List[CharPosition])], Double)] = {
		hashMap.mapValues(value => {
			val similarityValue = value.size.toDouble / numberOfHashes.toDouble
			(value, similarityValue)
		})
	}

	/**
	  * this function do, exactly what you expect
	  *
	  * @param hashMap
	  * @param threshold
	  * @return
	  */
	def filterHashMap(hashMap: Map[Long, (List[(String, List[CharPosition], List[CharPosition])], Double)], threshold: Double)
	: Map[Long, (List[(String, List[CharPosition], List[CharPosition])], Double)] = {
		hashMap.filter(value => {
			value._2._2 > threshold
		})
	}

	def convertToScalaTuple(dbtuple: com.mongodb.BasicDBList): (Int, Int, Int) = {
		dbtuple.asInstanceOf[(Int, Int, Int)]
	}

	def injectPosition(hashItems: List[(String, List[(Long, List[Int])])], table: Map[String, List[Int]])
	: List[(String, List[Int], List[(Long, List[Int])])] = {
		hashItems.map(value => {
			val positions = Try(table.get(value._1).get)
			if (positions.isFailure) print(positions.failed.get)
			(value._1, positions.get, value._2)
		})
	}

	def injectTitleAndText(response: Map[Long, (List[(String, List[CharPosition], List[CharPosition])], Double)],
	                       titles: Map[Long, String], wikiTexts: Map[Long, String])
	: Map[Long, (List[(String, List[CharPosition], List[CharPosition])], Double, String, String)] = {
		response.map(value => {
			val id = value._1
			id -> (value._2._1, value._2._2, titles.get(id).get, wikiTexts.get(id).get)
		})
	}

}