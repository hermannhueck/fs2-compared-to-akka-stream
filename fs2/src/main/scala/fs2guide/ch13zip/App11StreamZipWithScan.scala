package fs2guide.ch13zip

import fs2.Stream

object App11StreamZipWithScan extends App {

  println("\n-----")

  val zipped: List[(String, Int)] = Stream("uno", "dos", "tres", "cuatro").zipWithScan(0)(_ + _.length).toList
  println(zipped)
  assert(zipped == List(("uno", 0), ("dos", 3), ("tres", 6), ("cuatro", 10)))

  println("-----\n")
}
