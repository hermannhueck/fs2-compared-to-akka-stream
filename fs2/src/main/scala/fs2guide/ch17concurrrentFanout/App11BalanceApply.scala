package fs2guide.ch17concurrrentFanout

import cats.effect.{ContextShift, IO}
import fs2.Stream
import fs2.concurrent.Balance

import scala.concurrent.ExecutionContext

object App11BalanceApply extends App {

  println("\n-----")

  implicit val cs: ContextShift[IO] = IO.contextShift(ExecutionContext.global)

  val stream: Stream[IO, Int] = Stream.range(1, 11).covary[IO]

  val streamOfStreams: Stream[IO, Stream[IO, Unit]] =
    stream.through(Balance(chunkSize = 2)).map { worker: Stream[IO, Int] =>
      worker.evalMap { o => IO(println(">> ?: " + o.toString)) }
    }

  val joined: Stream[IO, Unit] = streamOfStreams.take(3).parJoinUnbounded
  joined.compile.drain.unsafeRunSync

  println("-----\n")
}
