package fs2guide.ch13zip

import fs2.Stream

object App10StreamZipWithPreviousAndNext extends App {

  println("\n-----")

  val zipped: List[(Option[String], String, Option[String])] = Stream("The", "quick", "brown", "fox").zipWithPreviousAndNext.toList
  println(zipped)

  val expected: List[(Option[String], String, Option[String])] = List(
    (None, "The", Some("quick")),
    (Some("The"), "quick", Some("brown")),
    (Some("quick"), "brown", Some("fox")),
    (Some("brown"), "fox", None)
  )

  assert(zipped == expected)

  println("-----\n")
}
