package de.htw.ai.wikiplag.viewindex

import scala.annotation.tailrec
import scala.collection.mutable.ListBuffer

/**
  * Created by robertsteiner on 27.05.16.
  */
object ViewIndexBuilderImp extends ViewIndexBuilder {

  /**
    * Erzeugt einen ViewIndex fuer den uebergebenen Text nach dem Schema:
    * List[(Int, Int, Int)]
    * [
    *   (wort_1, text.indexOf(wort_1), wort_1.length),
    *   (wort_2, text.indexOf(wort_2), wort_2.length), ...
    * ]
    * Die Reihenfolge der Woerter in der Liste muss gleich der Reihenfolge der Woerter im Text sein.
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
    * @param text           Der Text zu dem der ViewIndex erzeugt werden soll.
    * @param textAsWordList Die extrahierten Woerter des Textes als Liste.
    * @param viewIndex      Der ViewIndex.
    * @param searchIndex    Der Suchindex.
    * @param wordIndex      Der Wortindex.
    * @return Der ViewIndex.
    */
  @tailrec private def buildViewIndex(text: String,
                                      textAsWordList: List[String],
                                      viewIndex: ListBuffer[(Int, Int, Int)],
                                      searchIndex: Int,
                                      wordIndex: Int): List[(Int, Int, Int)] = {
    if (textAsWordList.size == wordIndex)
      viewIndex.toList
    else {
      val word = textAsWordList(wordIndex)
      val nextIndex = text.indexOf(word, searchIndex)
      if (nextIndex == -1)
        viewIndex.toList
      else
      {
        viewIndex.append((wordIndex, nextIndex, word.length))
        buildViewIndex(text, textAsWordList, viewIndex, nextIndex + word.length, wordIndex + 1)
      }
    }
  }

  /**
    * Erzeugt einen ViewIndex fuer den uebergebenen Text nach dem Schema:
    * List[(Int, Int, Int)]
    * [
    *   (wort_1, text.indexOf(wort_1), wort_1.length),
    *   (wort_2, text.indexOf(wort_2), wort_2.length), ...
    * ]
    * Die Reihenfolge der Woerter in der Liste muss gleich der Reihenfolge der Woerter im Text sein.
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
  def buildViewIndex(text: String, textWordsAsList: List[String]): List[(Int, Int, Int)] = {
    buildViewIndex(text, textWordsAsList, ListBuffer(), 0, 0)
  }
}
