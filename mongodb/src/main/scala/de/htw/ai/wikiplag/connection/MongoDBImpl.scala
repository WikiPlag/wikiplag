package de.htw.ai.wikiplag.connection

import com.mongodb.casbah.Imports._

/**
  * Created by Max on 30.04.2016.
  */
object MongoDBImpl extends MongoDBConn {

  /**
    * Open the Connection, with default values (HTW-Berlin)
    *
    * @param serverAddress MongoDB Server Address
    * @param credentials   Credentials for Login
    *
    */
  override def open(serverAddress: ServerAddress, credentials: MongoCredential): Unit = {
    mongoClient = MongoClient(serverAddress, List(credentials))
    wikiCollection = mongoClient(WikiDatabase)(WikiCollection)
  }

  /**
    * Close the Connection
    */
  override def close(): Unit = {
    if (mongoClient != null) {
      mongoClient.close()
    }
  }

  /**
    * Inserts a new Document into MongoDB
    *
    */
  override def insertDocument(wikiID: Long, title: String, hashes: Map[String, List[Int]], text: String, viewIndex: List[(Int, Int, Int)]): Unit = {
    val hashList = hashes.map(x => {
      Map("hash" -> x._1, "occurs" -> x._2)
    })

    val x = MongoDBObject(
      ("_id", wikiID),
      ("title", title),
      ("hashes", hashList),
      ("text", text),
      ("viewindex", viewIndex)
    )
    wikiCollection
      .insert(x)
  }

  /**
    * finds Similarities to the given Hashes bases on the given categories
    *
    * @param hashs     List of Hashes (N-Grams)
    * @param ngramSize ngram-size
    * @return List of [
    *         (Hash, List of (document_id, position))
    *         ]
    *         which contains the hashes
    */
  override def findSimilarity(hashs: List[String], ngramSize: Int): List[(String, List[(Long, Int)])] = {
    val ngramHashCollection = mongoClient(WikiDatabase)(WikiHashCollectionPrefix + ngramSize)
    val results = ngramHashCollection.aggregate(
      List(
        MongoDBObject("$match" -> ("hashes.hash" $in hashs)),
        MongoDBObject("$unwind" -> "$hashes"),
        MongoDBObject("$unwind" -> "$hashes.occurs"),
        MongoDBObject("$group" ->
          MongoDBObject(
            "_id" -> "$hashes.hash",
            "matches" -> MongoDBObject("$push" -> MongoDBObject(
              "wiki_id" -> "$_id",
              "occurs" -> "$hashes.occurs"
            )))
        )
      ),
      AggregationOptions(AggregationOptions.CURSOR)
    )

    results
      .filter(x => hashs.contains(x.get("_id")))
      .map(x => {
        val key = x.getAs[String]("_id").get
        val matches = x.getAs[List[BasicDBObject]]("matches").get
          .map(z => {
            (z.getLong("wiki_id"), z.getInt("occurs"))
          })
        (key, matches)
      })
      .toList
  }

  /**
    * Get the original (cleaned, but not hashed) text from the wiki page
    *
    * @param id id from the wiki article/document
    * @return some text
    */
  override def getOriginalText(id: Long): String = {
    // db.wiki.find( { "_id": { $eq: NumberLong(9095622) } }, {text: 1})
    wikiCollection
      .findOne(MongoDBObject("_id" -> id))
      .get
      .getAs[String]("text")
      .get
  }

  /**
    * Get the original (cleaned, but not hashed) text from the wiki page
    *
    * @param ids List of ids from the wiki articles/documents
    * @return Map of [WikiID, the text]
    */
  override def getOriginalText(ids: List[Long]): Map[Long, String] = {
    // db.wiki.find( { "_id": { $in: [NumberLong(9095622), NumberLong(3567931)] } }, {text: 1})
    wikiCollection
      .find("_id" $in ids, MongoDBObject("text" -> 1))
      .map(x => {
        (x.getAs[Long]("_id").get, x.getAs[String]("text").get)
      })
      .toMap
  }

  /**
    * Get the title from the wiki page
    *
    * @param id id from the wiki article/document
    * @return some text
    */
  override def getTitle(id: Long): String = {
    // db.wiki.find( { "_id": { $eq: NumberLong(9095622) } }, {title: 1})
    wikiCollection
      .findOne(MongoDBObject("_id" -> id), MongoDBObject("title" -> 1))
      .get
      .getAs[String]("title")
      .get
  }

  /**
    * Get the original (cleaned, but not hashed) text from the wiki page
    *
    * @param ids List of ids from the wiki articles/documents
    * @return Map of [WikiID, the text]
    */
  override def getTitle(ids: List[Long]): Map[Long, String] = {
    // db.wiki.find( { "_id": { $in: [NumberLong(9095622), NumberLong(3567931)] } }, {title: 1})
    wikiCollection
      .find("_id" $in ids, MongoDBObject("title" -> 1))
      .map(x => {
        (x.getAs[Long]("_id").get, x.getAs[String]("title").get)
      })
      .toMap
  }

  /**
    * Get the ViewIndex text from the wiki page
    *
    * @param id id from the wiki article/document
    * @return some text
    */
  override def getViewIndex(id: Long): List[(Int, Int, Int)] = {
    // db.wiki.find( { "_id": { $eq: NumberLong(9095622) } }, {viewindex: 1})
    wikiCollection
      .findOne(MongoDBObject("_id" -> id), MongoDBObject("viewindex" -> 1))
      .get
      .as[BasicDBList]("viewindex")
      .map(x => {
        val d = x.asInstanceOf[BasicDBList]
        (d(0).asInstanceOf[Int], d(1).asInstanceOf[Int], d(2).asInstanceOf[Int])
      })
      .toList
  }

  /**
    * Get the ViewIndex text from the wiki page
    *
    * @param ids id from the wiki article/document
    * @return some text
    */
  override def getViewIndex(ids: List[Long]): Map[Long, List[(Int, Int, Int)]] = {
    // db.wiki.find( { "_id": { $in: [NumberLong(9095622), NumberLong(3567931)] } }, {viewindex: 1})
    wikiCollection.find("_id" $in ids, MongoDBObject("viewindex" -> 1)).toList
      .map(monogoObject => (
        monogoObject.asInstanceOf[BasicDBObject].get("_id"),
        monogoObject.asInstanceOf[BasicDBObject].get("viewindex")
        )
      )
      .map(viewIndexObject => (
        viewIndexObject._1.asInstanceOf[Long],
        viewIndexObject._2.asInstanceOf[BasicDBList].toList
          .map(viewIndexElement => (
            viewIndexElement.asInstanceOf[BasicDBList].get(0).asInstanceOf[Int],
            viewIndexElement.asInstanceOf[BasicDBList].get(1).asInstanceOf[Int],
            viewIndexElement.asInstanceOf[BasicDBList].get(2).asInstanceOf[Int]
            )
          )
        )
      ).toMap
  }

}
