package fs2guide.ch13zip

import fs2.Stream

object App06StreamAllZipWith extends App {

  println("\n-----")

  val zipped: List[Int] = Stream(1, 2, 3).zipAllWith(Stream(4, 5, 6, 7))(0, 0)(_ + _).toList
  println(zipped)
  assert(zipped == List(5, 7, 9, 7))

  println("-----\n")
}
