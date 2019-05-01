package fs2guide.ch17concurrrentFanout

import cats.effect.{ContextShift, IO}
import fs2.{Pipe, Stream}

import scala.concurrent.ExecutionContext

object App15StreamBalanceThrough extends App {

  println("\n-----")

  implicit val cs: ContextShift[IO] = IO.contextShift(ExecutionContext.global)

  val stream: Stream[IO, Int] = Stream.range(1, 11).covary[IO]

  val pipe: Pipe[IO, Int, Unit] = worker =>
    worker.evalMap { o => IO(println(s">> ? " + o.toString)) }

  val joined: Stream[IO, Unit] = stream.balanceThrough(chunkSize = 2, maxConcurrent = 3)(pipe)
  joined.compile.drain.unsafeRunSync

  println("-----\n")
}
