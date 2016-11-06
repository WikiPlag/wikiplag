package com.wikiplag.webapp.models

case class ResponseInitInput (suspiciousDocs: List[SuspiciousDocs], nGramSize: Int)
case class SuspiciousDocs (docId: Long, hashKeyAndPositions: List[HashKeyAndPositions], similarityValue: Double, title:String, text: String)
case class HashKeyAndPositions (hash: String, wikiCharPositions: List[CharPosition], originCharPositions: List[CharPosition])
case class CharPosition(startCharIndex: Int, endCharIndex: Int)
