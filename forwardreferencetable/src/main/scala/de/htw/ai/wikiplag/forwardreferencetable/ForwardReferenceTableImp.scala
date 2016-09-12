package de.htw.ai.wikiplag.forwardreferencetable

import org.apache.commons.codec.digest.DigestUtils

/**
  * Created by robertsteiner on 27.05.16.
  */
object ForwardReferenceTableImp extends ForwardReferenceTable {
  /**
    * Erzeugt eine ForwardReferenceTable nach dem Schema:
    * {
    *   "hash("ngram_1")":  List[ ( page_id, List[ ngram_position_1, ngram_position_2, ngram_position_3 ] ) ],
    *   "hash("ngram_2")":  List[ ( page_id, List[ ngram_position_1, ... ] ) ], ...
    * }
    * Die Hashwerte der n-Gramme werden durch die SHA1 Hashfunktion brechnet und auf eine 40
    * Zeichen langen Hexadezimalzahl abgebildet.
    * Die Woerter in einem n-Gramm werden (bevor sie der Hashfunktion uebergeben werden)
    * durch Leerzeichen miteinander verbunden.
    *
    * Bespiel:
    * List[String]("kam", "die", "Parodie") -> hash("kam die Parodie")
    * Die n-Gramm-Positionen innerhalb der Liste sind aufsteigend sortiert, alles andere wird nicht sortiert.
    *
    * Beispiel:
    *
    * Input:
    * pageId = Int(1)
    * pageWordsAsList = List[String]("kam", "die", "Parodie", "An", "Alan", "Smithee", "Film", "Burn", "Hollywood")
    * stepSize = Int(3)
    *
    * Output:
    * collection.mutable.Map[String, List[(Int, List[Int])]
    * {
    *   "hash("kam die Parodie")": List[ ( 1, List[ 0 ] ) ],
    *   "hash("die Parodie An")": List[ ( 1, List[ 1 ] ) ],
    *   "hash("Parodie An Alan")": List[ ( 1, List[ 2 ] ) ], ...
    * }
    *
    * @param pageId          Die Page-ID.
    * @param pageWordsAsList Eine Liste, deren Elemente die Woerter der Page enthalten.
    * @param nGramStepSize   Die Schrittlaenge der n-Gramme.
    * @return Eine Forward Reference Table.
    */
  def buildForwardReferenceTable(pageId: Int,
                                 pageWordsAsList: List[String],
                                 nGramStepSize: Int): collection.mutable.Map[String, List[(Int, List[Int])]] = {
    val forwardReferenceTable = collection.mutable.Map[String, List[(Int, List[Int])]]()

    if (nGramStepSize <= pageWordsAsList.size) {
      val nGrams = pageWordsAsList.sliding(nGramStepSize).zipWithIndex

      nGrams.foreach {
        case (wordsInNGram, wordIndex) => updateForwardReferenceTable(wordsInNGram, wordIndex)
      }

      def updateForwardReferenceTable(wordsInNGram: List[String], wordIndex: Int): Unit = {
        val hashOfNGram = DigestUtils.sha1Hex(wordsInNGram.mkString(" "))
        forwardReferenceTable.update(hashOfNGram,
          updateTuple(forwardReferenceTable.getOrElse(hashOfNGram, List()), pageId, wordIndex))
      }

      def updateTuple(valueList: List[(Int, List[Int])], pageId: Int, wordIndex: Int): List[(Int, List[Int])] = {
        if (valueList.isEmpty)
          List((pageId, wordIndex :: List()))
        else
          List((valueList.head._1, valueList.head._2 ++ (wordIndex :: List())))
      }
    }
    forwardReferenceTable
  }

  /**
    * Erzeugt eine ForwardReferenceTable nach dem Schema:
    * {
    *   "hash("ngram_1")":  List[ ngram_position_1, ngram_position_2, ngram_position_3 ],
    *   "hash("ngram_2")":  List[ ngram_position_1, ... ], ...
    * }
    *
    * Die Hashwerte der n-Gramme werden durch die SHA1 Hashfunktion brechnet und auf eine 40
    * Zeichen langen Hexadezimalzahl abgebildet.
    * Die Woerter in einem n-Gramm werden (bevor sie der Hashfunktion uebergeben werden)
    * durch Leerzeichen miteinander verbunden.
    *
    * Bespiel:
    * List[String]("kam", "die", "Parodie") -> hash("kam die Parodie")
    * Die n-Gramm-Positionen innerhalb der Liste sind aufsteigend sortiert, alles andere wird nicht sortiert.
    *
    * Beispiel:
    *
    * Input:
    * pageWordsAsList = List[String]("kam", "die", "Parodie", "An", "Alan", "Smithee", "Film", "Burn", "Hollywood")
    * stepSize = Int(3)
    *
    * Output:
    * collection.mutable.Map[String, List[Int]]
    * {
    *   "hash("kam die Parodie")": List[ 0 ],
    *   "hash("die Parodie An")": List[ 1 ],
    *   "hash("Parodie An Alan")": List[ 2 ], ...
    * }
    *
    * @param pageWordsAsList Eine Liste, deren Elemente die Woerter der Page enthalten.
    * @param nGramStepSize   Die Schrittlaenge der n-Gramme.
    * @return Eine Forward Reference Table.
    */
  def buildForwardReferenceTable(pageWordsAsList: List[String],
                                 nGramStepSize: Int): collection.mutable.Map[String, List[Int]] = {
    val forwardReferenceTable = collection.mutable.Map[String, List[Int]]()
    if (nGramStepSize <= pageWordsAsList.size) {
      val nGrams = pageWordsAsList.sliding(nGramStepSize).zipWithIndex

      nGrams.foreach {
        case (wordsInNGram, wordIndex) => updateForwardReferenceTable(wordsInNGram, wordIndex)
      }

      def updateForwardReferenceTable(wordsInNGram: List[String], wordIndex: Int): Unit = {
        val hashOfNGram = DigestUtils.sha1Hex(wordsInNGram.mkString(" "))
        forwardReferenceTable.update(hashOfNGram, forwardReferenceTable.getOrElse(hashOfNGram, List()) ++ (wordIndex :: List()))
      }
    }
    forwardReferenceTable
  }
}
