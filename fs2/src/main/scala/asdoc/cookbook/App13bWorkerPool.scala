package asdoc.cookbook

import cats.effect.{ContextShift, IO}
import fs2.{Pipe, Stream}

import scala.concurrent.ExecutionContext

object App13bWorkerPool extends App {

  println("\n=====")

  implicit val cs: ContextShift[IO] = IO.contextShift(ExecutionContext.global)

  private val pipe: Pipe[IO, Int, String] = stream =>
    stream.evalMap {
      intValue => IO {val s = s"$intValue done"; println(s); s }
    }

  val balancedStream: Stream[IO, String] =
    Stream.range(1, 21)
      .covary[IO]
      .prefetchN(100)
      .balanceAvailable
      .map(pipe)
      .take(10)
      .parJoin(10)

  //  val result = balancedStream.compile.toVector.unsafeRunSync
  //  println(result)
  balancedStream.compile.drain.unsafeRunSync()


  println("=====\n")
}
