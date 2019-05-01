package fs2guide.ch13zip

import fs2.Stream

object App09StreamZipWithPrevious extends App {

  println("\n-----")

  val zipped: List[(Option[String], String)] = Stream("The", "quick", "brown", "fox").zipWithPrevious.toList
  println(zipped)
  assert(zipped == List((None, "The"), (Some("The"), "quick"), (Some("quick"), "brown"), (Some("brown"), "fox")))

  println("-----\n")
}
