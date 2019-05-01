package fs2guide.ch13zip

import fs2.Stream

object App08StreamZipWithNext extends App {

  println("\n-----")

  val zipped: List[(String, Option[String])] = Stream("The", "quick", "brown", "fox").zipWithNext.toList
  println(zipped)
  assert(zipped == List(("The", Some("quick")), ("quick", Some("brown")), ("brown", Some("fox")), ("fox", None)))

  println("-----\n")
}
