package fs2guide.ch17concurrrentFanout

import cats.effect.{ContextShift, IO}
import fs2.{Pipe, Stream}

import scala.concurrent.ExecutionContext

object App07StreamBroadcastTo extends App {

  println("\n-----")

  implicit val cs: ContextShift[IO] = IO.contextShift(ExecutionContext.global)

  val stream: Stream[IO, Int] = Stream(1, 2, 3, 4).covary[IO]

  val pipe: Pipe[IO, Int, Unit] = worker =>
    worker.evalMap { o => IO(println(s">> ? " + o.toString)) }

  val joined: Stream[IO, Unit] = stream.broadcastTo(maxConcurrent = 3)(pipe)
  joined.compile.drain.unsafeRunSync

  println("-----\n")
}
