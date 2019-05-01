package fs2guide.ch14evalmap

import cats.effect.IO
import fs2.{Pure, Stream}

object App03StreamEvalMapAccumulate extends App {

  println("\n-----")

  // ----- evalMapAccumulate - Like mapAccumulate, but accepts a function returning an F[_]

  val stream: Stream[IO, String] = Stream("Hello", "World").covary[IO]
  val accumulated: Stream[IO, (Int, Char)] = stream.evalMapAccumulate(0)((l, s) => IO(l + s.length, s.head))
  val vector: Vector[(Int, Char)] = accumulated.compile.toVector.unsafeRunSync()
  println(vector)
  assert(vector == Vector((5, 'H'), (10, 'W')))


  val stream2: Stream[IO, Int] = Stream(1, 2, 3, 4).covary[IO]
  private val accumulated2 = stream2.evalMapAccumulate(0)((acc, i) => IO((i, acc + i)))
  val vector2 = accumulated2.compile.toVector.unsafeRunSync
  println(vector2)
  assert(vector2 == Vector((1,1), (2,3), (3,5), (4,7)))

  println("-----\n")
}
