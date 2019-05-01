package fs2guide.ch14evalmap

import cats.effect.{ContextShift, IO}
import fs2.Stream

object App05StreamParEvalMapUnordered extends App {

  println("\n----- parEvalMapUnordered (resulting order of elements is NOT guaranteed)")

  // ----- parEvalMapUnordered: Like Stream#parEvalMap, but the order of the original stream is NOT retained.

  implicit val cs: ContextShift[IO] = IO.contextShift(scala.concurrent.ExecutionContext.Implicits.global)

  val s: Stream[IO, Int] = Stream(1, 2, 3, 4).covary[IO]

  s.parEvalMapUnordered(maxConcurrent = 2)(i => IO(println(i))).compile.drain.unsafeRunSync
  println("-----")

  val list = s.parEvalMapUnordered(maxConcurrent = 2)(i => IO{println(i); i*i}).compile.toList.unsafeRunSync
  println(list)


  // ----- mapAsyncUnordered is an alias for parEvalMapUnordered

  println("\n----- mapAsyncUnordered (resulting order of elements is NOT guaranteed)")
  s.mapAsyncUnordered(maxConcurrent = 2)(i => IO(println(i))).compile.drain.unsafeRunSync
  println("-----")

  val list2 = s.mapAsyncUnordered(maxConcurrent = 2)(i => IO{println(i); i*i}).compile.toList.unsafeRunSync
  println(list2)

  println("-----\n")
}
