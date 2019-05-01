package fs2guide.ch13zip

import fs2.Stream

object App05StreamZipWith extends App {

  println("\n-----")

  val zipped: List[Int] = Stream(1, 2, 3).zipWith(Stream(4, 5, 6, 7))(_ + _).toList
  println(zipped)
  assert(zipped == List(5, 7, 9))

  println("-----\n")
}
