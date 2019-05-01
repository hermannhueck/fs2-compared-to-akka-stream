package fs2guide.ch13zip

import fs2.Stream

object App12StreamZipWithScan1 extends App {

  println("\n-----")

  val zipped: List[(String, Int)] = Stream("uno", "dos", "tres", "cuatro").zipWithScan1(0)(_ + _.length).toList
  println(zipped)
  assert(zipped == List(("uno", 3), ("dos", 6), ("tres", 10), ("cuatro", 16)))

  println("-----\n")
}
