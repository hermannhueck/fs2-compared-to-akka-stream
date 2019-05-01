package fs2guide.ch04streamoperations

import fs2.Stream

object App01StreamOperations extends App {

  println("\n----- Stream operations")

  println(">>> Stream#append")
  val res1 = Stream(1, 2, 3).append(Stream(4, 5)).toList
  // res1: List[Int] = List(1, 2, 3, 4, 5)
  println(res1)

  println(">>> Stream#++")
  val res2 = (Stream(1, 2, 3) ++ Stream(4, 5)).toList
  // res2: List[Int] = List(1, 2, 3, 4, 5)
  println(res2)

  println(">>> Stream#map")
  val res3 = Stream(1, 2, 3).map(_ + 1).toList
  // res3: List[Int] = List(2, 3, 4)
  println(res3)

  println(">>> Stream#filter")
  val res4 = Stream(1, 2, 3).filter(_ % 2 != 0).toList
  // res4: List[Int] = List(1, 3)
  println(res4)

  println(">>> Stream#fold")
  val res5 = Stream(1, 2, 3).fold(0)(_ + _).toList
  // res5: List[Int] = List(6)
  println(res5)

  println(">>> Stream#collect")
  val res6 = Stream(None, Some(2), Some(3)).collect { case Some(i) => i }.toList
  // res6: List[Int] = List(2, 3)
  println(res6)

  println(">>> Stream#intersperse")
  val res7 = Stream.range(0, 5).intersperse(42).toList
  // res7: List[Int] = List(0, 42, 1, 42, 2, 42, 3, 42, 4)
  println(res7)

  println(">>> Stream#flatMap")
  val res8 = Stream(1, 2, 3).flatMap(i => Stream(i, i)).toList
  // res8: List[Int] = List(1, 1, 2, 2, 3, 3)
  println(res8)

  println(">>> Stream#repeat, Stream#take")
  val res9 = Stream(1, 2, 3).repeat.take(10).toList
  // res9: List[Int] = List(1, 2, 3, 1, 2, 3, 1, 2, 3, 1)
  println(res9)

  println(">>> Stream#repeatN")
  val res10 = Stream(1, 2, 3).repeatN(2).toList
  // res10: List[Int] = List(1, 2, 3, 1, 2, 3)
  println(res10)

  println("-----\n")
}
