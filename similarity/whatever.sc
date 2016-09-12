val ngOriginal = 50
val ngMatches = 25

def matchNgrams(ngOriginal:Int, ngMatches:Int):(Double) = {
  val result = ((ngOriginal).toDouble / 100) * ngMatches.toDouble
  return result
}

val result = matchNgrams(ngOriginal, ngMatches)


val listeWiki = List((123, List(1,2, 4, 10, 10005, 23)), (345, List(9, 1, 4, 6, 3)))
val lW = listeWiki.length

val listeOrig = List((123, List(345)), (56, List(9)), (56, List(9)), (56, List(9)), (56, List(9)), (123, List(345)), (56, List(9)), (56, List(9)), (56, List(9)), (56, List(9)))
val lO = listeOrig.length

val matches = matchNgrams(listeOrig.length, listeWiki.length)
val mathches2 = List(("hash", List(1, List(1,2,3))))


val matchesPerLink = matchNgrams(listeOrig.length, listeWiki(0)._2.length)


