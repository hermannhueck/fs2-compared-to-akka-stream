package fs2guide.ch14evalmap

import cats.effect.{ContextShift, IO}
import fs2.Stream

object App04StreamParEvalMap extends App {

  println("\n----- parEvalMap")

  // ----- parEvalMap: Like Stream#evalMap, but will evaluate effects in parallel, emitting the results downstream
  // in the same order as the input stream. The number of concurrent effects is limited by the maxConcurrent parameter.
  // The order of the original stream is retained.

  implicit val cs: ContextShift[IO] = IO.contextShift(scala.concurrent.ExecutionContext.Implicits.global)

  val s: Stream[IO, Int] = Stream(1, 2, 3, 4).covary[IO]

  s.parEvalMap(maxConcurrent = 2)(i => IO(println(i))).compile.drain.unsafeRunSync
  println("-----")

  val list = s.parEvalMap(maxConcurrent = 2)(i => IO{println(i); i*i}).compile.toList.unsafeRunSync
  println(list)


  // ----- mapAsync is an alias for parEvalMap

  println("\n----- mapAsync")
  s.mapAsync(maxConcurrent = 2)(i => IO(println(i))).compile.drain.unsafeRunSync
  println("-----")

  val list2 = s.mapAsync(maxConcurrent = 2)(i => IO{println(i); i*i}).compile.toList.unsafeRunSync
  println(list2)

  println("-----\n")
}
