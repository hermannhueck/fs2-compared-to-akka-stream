package fs2guide.ch13zip

import fs2.Stream

object App01StreamZip extends App {

  println("\n-----")

  val zipped: List[(Int, Int)] = Stream(1, 2, 3).zip(Stream(4, 5, 6, 7)).toList
  println(zipped)
  assert(zipped == List((1, 4), (2, 5), (3, 6)))

  println("-----\n")
}
