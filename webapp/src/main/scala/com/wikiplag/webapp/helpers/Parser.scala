package com.wikiplag.webapp.helpers

import de.htw.ai.wikiplag.parser.WikiDumpParser

import scala.annotation.tailrec
import scala.collection.mutable.ListBuffer

object Parser {

  def parsePlainText(text: String): List[String] = {
    WikiDumpParser.extractWords(text, ListBuffer(), 0)
  }

}
