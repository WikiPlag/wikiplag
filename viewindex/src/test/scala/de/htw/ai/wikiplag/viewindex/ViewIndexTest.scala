package de.htw.ai.wikiplag.viewindex

import org.junit.runner.RunWith
import org.scalatest.FunSuite
import org.scalatest.junit.JUnitRunner

/**
  * Created by robertsteiner on 18.06.16.
  */
@RunWith(classOf[JUnitRunner])
class ViewIndexTest extends FunSuite {

  trait TestViewIndexBuilder {
    val viewIndexBuilder = ViewIndexBuilderImp
  }

  test("testBuildViewIndex") {
    new TestViewIndexBuilder {
      val text = "Alan Smithee steht als Pseudonym für einen fiktiven Regisseur, der Filme verantwortet, bei " +
        "denen der eigentliche Regisseur seinen Namen nicht mit dem Werk in Verbindung gebracht haben möchte. " +
        "Von 1968 bis 2000 wurde es von der Directors Guild of America (DGA) für solche Situationen empfohlen, " +
        "seither ist es Thomas Lee. Alan Smithee ist jedoch weiterhin in Gebrauch."
      val wordList = List(
        "Alan", "Smithee", "steht", "als", "Pseudonym", "für", "einen", "fiktiven", "Regisseur",
        "der", "Filme", "verantwortet", "bei", "denen", "der", "eigentliche", "Regisseur", "seinen", "Namen",
        "nicht", "mit", "dem", "Werk", "in", "Verbindung", "gebracht", "haben", "möchte", "Von", "bis", "wurde",
        "es", "von", "der", "Directors", "Guild", "of", "America", "DGA", "für", "solche", "Situationen", "empfohlen",
        "seither", "ist", "es", "Thomas", "Lee", "Alan", "Smithee", "ist", "jedoch", "weiterhin", "in", "Gebrauch"
      )
      val expected = List(
        (0, 0, 4), (1, 5, 7), (2, 13, 5), (3, 19, 3), (4, 23, 9), (5, 33, 3), (6, 37, 5), (7, 43, 8),
        (8, 52, 9), (9, 63, 3), (10, 67, 5), (11, 73, 12), (12, 87, 3), (13, 91, 5), (14, 97, 3), (15, 101, 11),
        (16, 113, 9), (17, 123, 6), (18, 130, 5), (19, 136, 5), (20, 142, 3), (21, 146, 3), (22, 150, 4),
        (23, 155, 2), (24, 158, 10), (25, 169, 8), (26, 178, 5), (27, 184, 6), (28, 192, 3), (29, 201, 3),
        (30, 210, 5), (31, 216, 2), (32, 219, 3), (33, 223, 3), (34, 227, 9), (35, 237, 5), (36, 243, 2),
        (37, 246, 7), (38, 255, 3), (39, 260, 3), (40, 264, 6), (41, 271, 11), (42, 283, 9), (43, 294, 7),
        (44, 302, 3), (45, 306, 2), (46, 309, 6), (47, 316, 3), (48, 321, 4), (49, 326, 7), (50, 334, 3),
        (51, 338, 6), (52, 345, 9), (53, 355, 2), (54, 358, 8)
      )
      val result = viewIndexBuilder.buildViewIndex(text, wordList)

      assert(expected === result)
      assert(expected.length === result.length)
    }
  }

  test("testBuildViewIndex textWordsAsList.length > words in text") {
    new TestViewIndexBuilder {
      val text = "Alan Smithee steht als Pseudonym"
      val wordList = List(
        "Alan", "Smithee", "steht", "als", "Pseudonym", "für", "einen", "fiktiven", "Regisseur",
        "der", "Filme", "verantwortet", "bei", "denen", "der", "eigentliche", "Regisseur", "seinen", "Namen",
        "nicht", "mit", "dem", "Werk", "in", "Verbindung", "gebracht", "haben", "möchte", "Von", "bis", "wurde",
        "es", "von", "der", "Directors", "Guild", "of", "America", "DGA", "für", "solche", "Situationen", "empfohlen",
        "seither", "ist", "es", "Thomas", "Lee", "Alan", "Smithee", "ist", "jedoch", "weiterhin", "in", "Gebrauch"
      )
      val expected = List(
        (0, 0, 4), (1, 5, 7), (2, 13, 5), (3, 19, 3), (4, 23, 9)
      )
      val result = viewIndexBuilder.buildViewIndex(text, wordList)

      assert(expected === result)
      assert(expected.length === result.length)
    }
  }

  test("testBuildViewIndex textWordsAsList.length < words in text") {
    new TestViewIndexBuilder {
      val text = "Alan Smithee steht als Pseudonym für einen fiktiven Regisseur, der Filme verantwortet, bei " +
        "denen der eigentliche Regisseur seinen Namen nicht mit dem Werk in Verbindung gebracht haben möchte. " +
        "Von 1968 bis 2000 wurde es von der Directors Guild of America (DGA) für solche Situationen empfohlen, " +
        "seither ist es Thomas Lee. Alan Smithee ist jedoch weiterhin in Gebrauch."
      val wordList = List(
        "Alan", "Smithee", "steht", "als", "Pseudonym"
      )
      val expected = List(
        (0, 0, 4), (1, 5, 7), (2, 13, 5), (3, 19, 3), (4, 23, 9)
      )
      val result = viewIndexBuilder.buildViewIndex(text, wordList)

      assert(expected === result)
      assert(expected.length === result.length)
    }
  }

  test("testBuildViewIndex textWordsAsList != words in text") {
    new TestViewIndexBuilder {
      val text = "Alan Smithee steht als Pseudonym für einen fiktiven Regisseur, der Filme verantwortet, bei " +
        "denen der eigentliche Regisseur seinen Namen nicht mit dem Werk in Verbindung gebracht haben möchte. " +
        "Von 1968 bis 2000 wurde es von der Directors Guild of America (DGA) für solche Situationen empfohlen, " +
        "seither ist es Thomas Lee. Alan Smithee ist jedoch weiterhin in Gebrauch."
      val wordList = List(
        "a", "b", "c", "d", "e"
      )
      val expected = List()
      val result = viewIndexBuilder.buildViewIndex(text, wordList)

      assert(expected === result)
      assert(expected.length === result.length)
    }
  }

  test("testBuildViewIndex empty text") {
    new TestViewIndexBuilder {
      val text = ""
      val wordList = List(
        "a", "b", "c", "d", "e"
      )
      val expected = List()
      val result = viewIndexBuilder.buildViewIndex(text, wordList)

      assert(expected === result)
      assert(expected.length === result.length)
    }
  }

  test("testBuildViewIndex text=null") {
    new TestViewIndexBuilder {
      val text = null
      val wordList = List(
        "a", "b", "c", "d", "e"
      )
      val expected = null
      val result = intercept[NullPointerException] {
        viewIndexBuilder.buildViewIndex(text, wordList)
      }

      assert(result.getMessage === expected)
    }
  }

  test("testBuildViewIndex empty textWordsAsList") {
    new TestViewIndexBuilder {
      val text = "Alan"
      val wordList = List()
      val expected = List()
      val result = viewIndexBuilder.buildViewIndex(text, wordList)

      assert(expected === result)
      assert(expected.length === result.length)
    }
  }

  test("testBuildViewIndex textWordsAsList=null") {
    new TestViewIndexBuilder {
      val text = "Alan"
      val wordList = null
      val expected = null
      val result = intercept[NullPointerException] {
        viewIndexBuilder.buildViewIndex(text, wordList)
      }

      assert(result.getMessage === expected)
    }
  }
}