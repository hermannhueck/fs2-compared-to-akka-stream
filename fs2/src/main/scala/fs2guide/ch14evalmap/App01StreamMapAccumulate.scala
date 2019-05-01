package fs2guide.ch14evalmap

import fs2.{Pure, Stream}

object App01StreamMapAccumulate extends App {

  println("\n-----")

  // ----- mapAccumulate maps a running total according to initial value and the input with the function f

  val stream: Stream[Pure, String] = Stream("Hello", "World")
  val accumulated: Stream[Pure, (Int, Char)] = stream.mapAccumulate(0)((l, s) => (l + s.length, s.head))
  val vector: Vector[(Int, Char)] = accumulated.toVector
  println(vector)
  assert(vector == Vector((5, 'H'), (10, 'W')))

  println("-----\n")
}
