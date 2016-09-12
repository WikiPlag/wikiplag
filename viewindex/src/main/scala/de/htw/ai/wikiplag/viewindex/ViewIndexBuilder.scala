package de.htw.ai.wikiplag.viewindex

/**
  * Created by robertsteiner on 25.05.16.
  */
trait ViewIndexBuilder {
  /**
    * Erzeugt einen ViewIndex fuer den uebergebenen Text nach dem Schema:
    * List[(Int, Int, Int)]
    * [
    *   (word_index_1, text.indexOf(wort_1), wort_1.length),
    *   (word_index_2, text.indexOf(wort_2), wort_2.length), ...
    * ]
    * Die Reihenfolge der Woerter in der Liste muss gleich der Reihenfolge der Woerter im Text sein.
    * Der Wort-Index beginnt mit dem Wert 0. Die Komponeten des Viewindeces sind nach dem Wort-Index
    * aufsteigend sortiert.
    *
    * Beispiel:
    *
    * Input:
    * text = String("1997 kam die Parodie An Alan Smithee Film: Burn Hollywood")
    * textWordsAsList = List[String]("kam", "die", "Parodie", "An", "Alan", "Smithee", "Film", "Burn", "Hollywood")
    *
    * Output:
    * List[(Int, Int, Int)]((0, 5, 3), (1, 9, 3), (2, 13, 7), (3, 21, 2), (4, 24, 4), …)
    *
    * @param text            Der Text zu dem der ViewIndex erzeugt werden soll.
    * @param textWordsAsList Die extrahierten Woerter des Textes als Liste.
    * @return Der ViewIndex.
    */
  def buildViewIndex(text: String, textWordsAsList: List[String]): List[(Int, Int, Int)]
}
