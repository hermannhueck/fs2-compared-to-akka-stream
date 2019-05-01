package fs2guide.ch13zip

import fs2.Stream

object App07StreamZipWithIndex extends App {

  println("\n-----")

  val zipped: List[(String, Long)] = Stream("The", "quick", "brown", "fox").zipWithIndex.toList
  println(zipped)
  assert(zipped == List(("The", 0), ("quick", 1), ("brown", 2), ("fox", 3)))

  println("-----\n")
}
