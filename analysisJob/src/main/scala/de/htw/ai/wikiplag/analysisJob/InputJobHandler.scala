package de.htw.ai.wikiplag.analysisJob

import com.mongodb.{MongoCredential, ServerAddress}

import de.htw.ai.wikiplag.forwardreferencetable.ForwardReferenceTableImp.buildForwardReferenceTable
import de.htw.ai.wikiplag.parser.WikiDumpParser.extractPlainText
import de.htw.ai.wikiplag.connection.MongoDBImpl

object InputJobHandler {

  def handleJob(text: String, step: Int): Any= {
    print(text)
    val tokens = extractPlainText(text)
    val hashes = buildForwardReferenceTable(tokens.map(_.toLowerCase()), step).toMap
   
   MongoDBImpl.open(
      new ServerAddress("hadoop03.f4.htw-berlin.de", 27020),
      MongoCredential.createCredential("REPLACE-ME", "REPLACE-ME", "REPLACE-ME".toCharArray)
    )
   
    val siml = MongoDBImpl.findSimilarity(hashes.keys.toList, 7)
    val fitSim = siml.map( hashSim =>
      (hashSim._1, hashSim._2.groupBy(_._1).mapValues(list=>list.map(x=> x._2)).toList )
    )
    MongoDBImpl.close()
    //generateWhitelist(fitSim, 30)
  }

  def generateWhitelist(ngMatches:List[(String, List[(String,List[Int])])], threshold:Double)
  :List[(String, List[(String, List[Int])], List[(String, Double)])] = {
    val filteredNg = filterMatches(ngMatches, threshold)
    filteredNg.map(item => (item._1, item._2, item._2.map(doc=> (doc._1, (item._2.size / ngMatches.size).toDouble))))
  }

  def filterMatches(ngMatches:List[(String, List[(String,List[Int])])], threshold:Double) :
  List[(String, List[(String,List[Int])])] = {
    val matchesSize = ngMatches.size
    ngMatches.filter(item => item._2.size / matchesSize > threshold)
  }

}
