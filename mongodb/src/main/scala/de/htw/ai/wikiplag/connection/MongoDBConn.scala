package de.htw.ai.wikiplag.connection

import com.mongodb.casbah.Imports._

/**
  * Created by Max M. on 18.05.2016.
  */
trait MongoDBConn {

  protected final val WikiDatabase = "s0546921"
  protected final val WikiCollection = "wiki"
  protected final val WikiHashCollectionPrefix = "wiki"

  protected var mongoClient: MongoClient = _
  protected var wikiCollection: MongoCollection = _


  /**
    * Open the Connection, with default values (HTW-Berlin)
    *
    * @param serverAddress MongoDB Server Address
    * @param credentials   Credentials for Login
    *
    */
  def open(serverAddress: ServerAddress, credentials: MongoCredential)

  /**
    * Close the Connection
    */
  def close()

  /**
    * Inserts a new Document into MongoDB
    *
    */
  def insertDocument(wikiID: Long, title: String, map: Map[String, List[Int]], text: String, viewIndex: List[(Int, Int, Int)]): Unit

  /**
    * finds Similarities to the given Hashes bases on the given categories
    *
    * @param list List of Hashes (N-Grams)
    * @param ngramSize ngram-size
    * @return List of [
    *         (Hash, List of (document_id, position))
    *         ]
    *         which contains the hashes
    */
  def findSimilarity(list: List[String], ngramSize: Int): List[(String, List[(Long, Int)])]

  /**
    * Get the original (cleaned, but not hashed) text from the wiki page
    *
    * @param id id from the wiki article/document
    * @return some text
    */
  def getOriginalText(id: Long): String

  /**
    * Get the original (cleaned, but not hashed) text from the wiki page
    *
    * @param ids List of ids from the wiki articles/documents
    * @return Map of [WikiID, the text]
    */
  def getOriginalText(ids: List[Long]): Map[Long, String]

  /**
    * Get the title from the wiki page
    *
    * @param id id from the wiki article/document
    * @return some title
    */
  def getTitle(id: Long): String

  /**
    * Get the original (cleaned, but not hashed) text from the wiki page
    *
    * @param ids List of ids from the wiki articles/documents
    * @return Map of [WikiID, the text]
    */
  def getTitle(ids: List[Long]): Map[Long, String]

  /**
    * Get the ViewIndex text from the wiki page
    *
    * @param id id from the wiki article/document
    * @return some viewindex
    */
  def getViewIndex(id: Long): List[(Int, Int, Int)]

  /**
    * Get the ViewIndex text from the wiki page
    *
    * @param ids id from the wiki article/document
    * @return some text
    */
  def getViewIndex(ids: List[Long]): Map[Long, List[(Int, Int, Int)]]

}
