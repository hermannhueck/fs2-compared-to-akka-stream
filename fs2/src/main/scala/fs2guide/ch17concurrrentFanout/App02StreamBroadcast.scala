package fs2guide.ch17concurrrentFanout

import cats.effect.{ContextShift, IO}
import fs2.Stream

import scala.concurrent.ExecutionContext

object App02StreamBroadcast extends App {

  println("\n-----")

  implicit val cs: ContextShift[IO] = IO.contextShift(ExecutionContext.global)

  val stream: Stream[IO, Int] = Stream(1, 2, 3, 4).covary[IO]

  val streamOfStreams: Stream[IO, Stream[IO, Unit]] =
    stream.broadcast.map { worker: Stream[IO, Int] =>
    // same as: stream.through(Broadcast(minReady = 1)).map { worker: Stream[IO, Int] =>
      worker.evalMap { o => IO(println(">> ?: " + o.toString)) }
    }

  val joined: Stream[IO, Unit] = streamOfStreams.take(3).parJoinUnbounded
  joined.compile.drain.unsafeRunSync

  println("-----\n")
}
