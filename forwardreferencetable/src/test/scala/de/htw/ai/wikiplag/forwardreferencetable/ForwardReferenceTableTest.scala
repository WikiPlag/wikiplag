package de.htw.ai.wikiplag.forwardreferencetable

import org.scalatest.FunSuite
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner

/**
  * Created by robertsteiner on 18.06.16.
  */
@RunWith(classOf[JUnitRunner])
class ForwardReferenceTableTest extends FunSuite {

  trait TestFRT {
    val frt = ForwardReferenceTableImp
  }

  test("testBuildForwardReferenceTable(wordList, nGramStepSize)") {
    new TestFRT {
      val wordList = List("Alan", "Smithee", "steht", "als", "Pseudonym")
      val nGramStepSize = 2
      val expected = collection.mutable.Map(
        "f6472d381b0666653d5cbad593e48759acbee713" -> List(1),
        "933bd4fc06c671a344eddc15066316cb3ed65dc1" -> List(2),
        "9a97100a462ad34a3d1d191f0aa473df311f4728" -> List(0),
        "6f8621c4e027479f83e8deeef88b4c416f1ed837" -> List(3)
      )
      val result = frt.buildForwardReferenceTable(wordList, nGramStepSize)

      assert(expected === result)
      assert(expected.keys.size === result.keys.size)
    }
  }

  test("testBuildForwardReferenceTable(wordList, nGramStepSize) duplicate hashes") {
    new TestFRT {
      val wordList = List("Alan", "Smithee", "steht", "Alan", "Smithee", "Alan", "Smithee")
      val nGramStepSize = 2
      val expected = collection.mutable.Map(
        "e3cc695c9a5128b038564dac97cbbe74bb9d16dc" -> List(2),
        "f6472d381b0666653d5cbad593e48759acbee713" -> List(1),
        "7a9092cb3dc9bba99dc606384e4c9868ec8fd79f" -> List(4),
        "9a97100a462ad34a3d1d191f0aa473df311f4728" -> List(0, 3, 5)
      )
      val result = frt.buildForwardReferenceTable(wordList, nGramStepSize)

      assert(expected === result)
      assert(expected.keys.size === result.keys.size)
    }
  }

  test("testBuildForwardReferenceTable(wordList, nGramStepSize) unicode strings") {
    new TestFRT {
      val wordList = List("Ä", "Ü", "Ö", "Ελλάδα", "Elláda")
      val nGramStepSize = 2
      val expected = collection.mutable.Map(
        "3a31d65e0399dbe13be12a2a31601f2ef795b7a7" -> List(2),
        "c3880848d57adde7417dd69136af96f8646e458c" -> List(0),
        "aea76b140cf54634f7e0d57950e3daf634ca6217" -> List(3),
        "6c66e195df9ba8da47aa4f5fe8dcd80461a25b94" -> List(1)
      )
      val result = frt.buildForwardReferenceTable(wordList, nGramStepSize)

      assert(expected === result)
      assert(expected.keys.size === result.keys.size)
    }
  }

  test("testBuildForwardReferenceTable(wordList, nGramStepSize) duplicate hashes, unicode strings") {
    new TestFRT {
      val wordList = List("Ä", "Ü", "Ö", "Ελλάδα", "Elláda", "Ä", "Ü", "Ö", "Ελλάδα", "Elláda")
      val nGramStepSize = 2
      val expected = collection.mutable.Map(
        "3a31d65e0399dbe13be12a2a31601f2ef795b7a7" -> List(2, 7),
        "c3880848d57adde7417dd69136af96f8646e458c" -> List(0, 5),
        "aea76b140cf54634f7e0d57950e3daf634ca6217" -> List(3, 8),
        "6c66e195df9ba8da47aa4f5fe8dcd80461a25b94" -> List(1, 6),
        "0ed9ba6ced687510ff2b041376127b728080c55a" -> List(4)
      )
      val result = frt.buildForwardReferenceTable(wordList, nGramStepSize)

      assert(expected === result)
      assert(expected.keys.size === result.keys.size)
    }
  }

  test("testBuildForwardReferenceTable(pageId, wordList, nGramStepSize)") {
    new TestFRT {
      val wordList = List("Alan", "Smithee", "steht", "als", "Pseudonym")
      val nGramStepSize = 2
      val pageId = 0
      val expected = collection.mutable.Map(
        "f6472d381b0666653d5cbad593e48759acbee713" -> List((0, List(1))),
        "933bd4fc06c671a344eddc15066316cb3ed65dc1" -> List((0, List(2))),
        "9a97100a462ad34a3d1d191f0aa473df311f4728" -> List((0, List(0))),
        "6f8621c4e027479f83e8deeef88b4c416f1ed837" -> List((0, List(3)))
      )
      val result = frt.buildForwardReferenceTable(pageId, wordList, nGramStepSize)

      assert(expected === result)
      assert(expected.keys.size === result.keys.size)
    }
  }

  test("testBuildForwardReferenceTable(pageId, wordList, nGramStepSize) duplicate hashes") {
    new TestFRT {
      val wordList = List("Alan", "Smithee", "steht", "Alan", "Smithee", "Alan", "Smithee")
      val nGramStepSize = 2
      val pageId = 0
      val expected = collection.mutable.Map(
        "e3cc695c9a5128b038564dac97cbbe74bb9d16dc" -> List((0, List(2))),
        "f6472d381b0666653d5cbad593e48759acbee713" -> List((0, List(1))),
        "7a9092cb3dc9bba99dc606384e4c9868ec8fd79f" -> List((0, List(4))),
        "9a97100a462ad34a3d1d191f0aa473df311f4728" -> List((0, List(0, 3, 5)))
      )
      val result = frt.buildForwardReferenceTable(pageId, wordList, nGramStepSize)

      assert(expected === result)
      assert(expected.keys.size === result.keys.size)
    }
  }

  test("testBuildForwardReferenceTable(pageId, wordList, nGramStepSize) unicode strings") {
    new TestFRT {
      val wordList = List("Ä", "Ü", "Ö", "Ελλάδα", "Elláda")
      val nGramStepSize = 2
      val pageId = 0
      val expected = collection.mutable.Map(
        "3a31d65e0399dbe13be12a2a31601f2ef795b7a7" -> List((0, List(2))),
        "c3880848d57adde7417dd69136af96f8646e458c" -> List((0, List(0))),
        "aea76b140cf54634f7e0d57950e3daf634ca6217" -> List((0, List(3))),
        "6c66e195df9ba8da47aa4f5fe8dcd80461a25b94" -> List((0, List(1)))
      )
      val result = frt.buildForwardReferenceTable(pageId, wordList, nGramStepSize)

      assert(expected === result)
      assert(expected.keys.size === result.keys.size)
    }
  }

  test("testBuildForwardReferenceTable(pageId, wordList, nGramStepSize) duplicate hashes, unicode strings") {
    new TestFRT {
      val wordList = List("Ä", "Ü", "Ö", "Ελλάδα", "Elláda", "Ä", "Ü", "Ö", "Ελλάδα", "Elláda")
      val nGramStepSize = 2
      val pageId = 0
      val expected = collection.mutable.Map(
        "3a31d65e0399dbe13be12a2a31601f2ef795b7a7" -> List((0, List(2, 7))),
        "c3880848d57adde7417dd69136af96f8646e458c" -> List((0, List(0, 5))),
        "aea76b140cf54634f7e0d57950e3daf634ca6217" -> List((0, List(3, 8))),
        "6c66e195df9ba8da47aa4f5fe8dcd80461a25b94" -> List((0, List(1, 6))),
        "0ed9ba6ced687510ff2b041376127b728080c55a" -> List((0, List(4)))
      )
      val result = frt.buildForwardReferenceTable(pageId, wordList, nGramStepSize)

      assert(expected === result)
      assert(expected.keys.size === result.keys.size)
    }
  }

  test("testBuildForwardReferenceTable(pageId, wordList, nGramStepSize) keys equals testBuildForwardReferenceTable(wordList, nGramStepSize) keys") {
    new TestFRT {
      val wordList = List("Alan", "Smithee", "steht", "Alan", "Smithee", "Alan", "Smithee")
      val nGramStepSize = 2
      val pageId = 0
      val result1 = frt.buildForwardReferenceTable(wordList, nGramStepSize)
      val result2 = frt.buildForwardReferenceTable(pageId, wordList, nGramStepSize)

      assert(result1.keys.toList.sorted === result2.keys.toList.sorted)
    }
  }

  test("testBuildForwardReferenceTable(pageId, wordList=unicode strings, nGramStepSize) keys equals testBuildForwardReferenceTable(wordList=unicode strings, nGramStepSize) keys") {
    new TestFRT {
      val wordList = List("Ä", "Ü", "Ö", "Ελλάδα", "Elláda", "Ä", "Ü", "Ö", "Ελλάδα", "Elláda")
      val nGramStepSize = 2
      val pageId = 0
      val result1 = frt.buildForwardReferenceTable(wordList, nGramStepSize)
      val result2 = frt.buildForwardReferenceTable(pageId, wordList, nGramStepSize)

      assert(result1.keys.toList.sorted === result2.keys.toList.sorted)
    }
  }

  test("testBuildForwardReferenceTable(wordList, nGramStepSize) negative nGram size") {
    new TestFRT {
      val wordList = List("Alan", "Smithee", "steht", "als", "Pseudonym")
      val nGramStepSize = -2
      val expected = "requirement failed: size=-2 and step=1, but both must be positive"
      val result = intercept[IllegalArgumentException] {
        frt.buildForwardReferenceTable(wordList, nGramStepSize)
      }

      assert(expected === result.getMessage)
    }
  }

  test("testBuildForwardReferenceTable(pageId, wordList, nGramStepSize) negative nGram size") {
    new TestFRT {
      val wordList = List("Alan", "Smithee", "steht", "als", "Pseudonym")
      val nGramStepSize = -2
      val pageId = 0
      val expected = "requirement failed: size=-2 and step=1, but both must be positive"
      val result = intercept[IllegalArgumentException] {
        frt.buildForwardReferenceTable(pageId, wordList, nGramStepSize)
      }

      assert(expected === result.getMessage)
    }
  }

  test("testBuildForwardReferenceTable(wordList, nGramStepSize) empty wordList") {
    new TestFRT {
      val wordList = List()
      val nGramStepSize = 2
      val expected = collection.mutable.Map[String, List[(Int, List[Int])]]()
      val result = frt.buildForwardReferenceTable(wordList, nGramStepSize)

      assert(expected === result)
    }
  }

  test("testBuildForwardReferenceTable(wordList, nGramStepSize) nGram size > wordList.length") {
    new TestFRT {
      val wordList = List("test")
      val nGramStepSize = 2
      val expected = collection.mutable.Map[String, List[(Int, List[Int])]]()
      val result = frt.buildForwardReferenceTable(wordList, nGramStepSize)

      assert(expected === result)
    }
  }

  test("testBuildForwardReferenceTable(pageId, wordList, nGramStepSize) empty wordList") {
    new TestFRT {
      val wordList = List()
      val nGramStepSize = 2
      val pageId = 0
      val expected = collection.mutable.Map[String, List[(Int, List[Int])]]()
      val result = frt.buildForwardReferenceTable(pageId, wordList, nGramStepSize)

      assert(expected === result)
    }
  }

  test("testBuildForwardReferenceTable(pageId, wordList, nGramStepSize) nGram size > wordList.length") {
    new TestFRT {
      val wordList = List("test")
      val nGramStepSize = 2
      val pageId = 0
      val expected = collection.mutable.Map[String, List[(Int, List[Int])]]()
      val result = frt.buildForwardReferenceTable(pageId, wordList, nGramStepSize)

      assert(expected === result)
    }
  }

  test("testBuildForwardReferenceTable(wordList, nGramStepSize) wordList null") {
    new TestFRT {
      val wordList = null
      val nGramStepSize = 2
      val expected = null
      val result = intercept[NullPointerException] {
        frt.buildForwardReferenceTable(wordList, nGramStepSize)
      }

      assert(result.getMessage === expected)
    }
  }

  test("testBuildForwardReferenceTable(pageId, wordList, nGramStepSize) wordList null") {
    new TestFRT {
      val wordList = null
      val nGramStepSize = 2
      val pageId = 0
      val expected = null
      val result = intercept[NullPointerException] {
        frt.buildForwardReferenceTable(pageId, wordList, nGramStepSize)
      }

      assert(result.getMessage === expected)
    }
  }
}