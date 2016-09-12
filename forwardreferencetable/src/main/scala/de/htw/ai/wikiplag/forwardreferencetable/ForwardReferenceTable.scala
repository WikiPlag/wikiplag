package de.htw.ai.wikiplag.forwardreferencetable

/**
  * Created by robertsteiner on 27.05.16.
  */
trait ForwardReferenceTable {
  /**
    * Erzeugt eine ForwardReferenceTable nach dem Schema:
    * {
    *   "hash("ngram_1")":  List[ ( page_id, List[ ngram_position_1, ngram_position_2, ngram_position_3 ] ) ],
    *   "hash("ngram_2")":  List[ ( page_id, List[ ngram_position_1, ... ] ) ], ...
    * }
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
                                 nGramStepSize: Int): collection.mutable.Map[String, List[(Int, List[Int])]]

  /**
    * Erzeugt eine ForwardReferenceTable nach dem Schema:
    * {
    *   "hash("ngram_1")":  List[ ngram_position_1, ngram_position_2, ngram_position_3 ],
    *   "hash("ngram_2")":  List[ ngram_position_1, ... ], ...
    * }
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
                                 nGramStepSize: Int): collection.mutable.Map[String, List[Int]]
}
