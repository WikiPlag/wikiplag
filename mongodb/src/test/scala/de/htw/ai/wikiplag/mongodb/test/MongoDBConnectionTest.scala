package de.htw.ai.wikiplag.mongodb.test

import de.htw.ai.wikiplag.connection.MongoDBImpl

/**
  * Created by Max on 23.05.2016.
  */
object MongoDBConnectionTest {

  def main(args: Array[String]) {

    MongoDBImpl.open(
      new ServerAddress("hadoop03.f4.htw-berlin.de", 27020),
      MongoCredential.createCredential("REPLACE-ME", "REPLACE-ME", "REPLACE-ME".toCharArray)
    )


    // findSimilarity

    //val x = MongoDBImpl.findSimilarity(List("be0c3b781a56427ad705014b382b5253314982c6"), 7)
    //x.foreach(println(_))

    println("##########################")
    println("####single requests#######")
    println("##########################")

//    val id = 34463L
    val id = 1L
    val orginal = MongoDBImpl.getOriginalText(id)
    println(orginal)

    val title = MongoDBImpl.getTitle(id)
    println(title)

    val vidx = MongoDBImpl.getViewIndex(id)
    println(vidx)

    println("##########################")
    println("####multiple requests#####")
    println("##########################")

//    val ids = List(1L, 87896L)
    val ids = List(1L, 3567931L)
    val orginals = MongoDBImpl.getOriginalText(ids)
    println(orginals)

    val titles = MongoDBImpl.getTitle(ids)
    println(titles)

    val vidxxx = MongoDBImpl.getViewIndex(ids)
    vidxxx.foreach(x => println(x._1, x._2.size))

    MongoDBImpl.close()
    println("Done")
  }

}

