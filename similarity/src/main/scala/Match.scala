import scala.util.parsing.json.{JSONArray, JSONObject}

object Match {

  /**
    * calculates the percentage of found matches in the original document
    *
    * @param ngOriginal total amount of n-grams in the original document
    * @param ngMatches  n-gram matches found in the wikibase
    * @return           value between 0 and 100
    */
  def matchNGrams(ngOriginal:Int, ngMatches:Int):(Double) = {
    ((ngOriginal).toDouble / 100) * ngMatches.toDouble
  }



  def countNgrams(ngList:List[_]):Int = {
    return ngList.length
  }


  // Schwellenwert für Weitergabe ans Frontend
  /**
    * Generates whitelist
    * Program with tailrecursion
    *
    * Für jeden Hash (nGram) errechne den matchValue (matchNGrams).
    * Wenn der mV > threshold übernehme das jeweilige Doc in die Whitelist, rekursiver Aufruf für jede DocID bis liste Leer
    * Wenn der mV < threshold verwerfe und rekursiver Aufruf für nächste DocID bis Liste leer
    * Übergebe Whitelist ans Frontend
    *
    * @param threshold
    * @return
    */
//  def generateWhitelist(threshold:Double, ngMatches:List[(String, List[Int,List[Int]])]):List[(Hash, List[(Doc_Id:Int,
//    List[Index:Int)], List[(Doc_Id_match:Int, matchValue:Double)]])] = threshold match {
//  }

  def generateWhitelist(ngMatches:List[(String, List[(Int,List[Int])])], threshold:Double)
    :List[(String, List[(Int, List[Int])], List[(Int, Double)])] = ???

  /**
    * Whitelist to JSON for FrontEnd
    */
  def toJson(data: List[(String, List[(Int, List[Int])], List[(Int, Double)])]): String = {
    JSONObject(toMap(data)
      .mapValues(value=> JSONArray(value.map{ case (matches, value)=>
        (JSONObject(matches.mapValues(JSONArray(_))), JSONObject(value))})
      )
    ).toString()
  }


  /**
    * Prepare data for converting to JSON
    * Assuming we do not have duplicates.
    *
    * @param data
    * @return Map that can be converted into json string
    */
  def toMap(data: List[(String, List[(Int, List[Int])], List[(Int, Double)])])
  :Map[String, List[(Map[String,List[Int]],Map[String, Double])]] = {

    data.map{ case (hash, matches, value) =>
      (hash, matches.toMap, value.toMap)
    }.groupBy(_._1).mapValues(_.map{case(hash, matches, value) =>
      (matches.map(x => (x._1.toString, x._2)), value.map(x=> (x._1.toString, x._2)))
    })
  }

  //TODO fork matcher von MongoDB
  //TODO Beispiel WikiTestcases
  //Skript um die Scala Operationen anzustossen: Input nGramme und mit Ergebniss MongoMatcher aufrufen

}
