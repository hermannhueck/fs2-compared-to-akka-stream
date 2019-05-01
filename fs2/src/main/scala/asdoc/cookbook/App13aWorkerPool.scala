package asdoc.cookbook

import cats.effect.{ContextShift, IO, Timer}
import fs2.{Pipe, Stream}

import scala.concurrent.ExecutionContext
import scala.concurrent.duration._

object App13aWorkerPool extends App {

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
      .balance(chunkSize = 10) // same as: .through(Balance(chunkSize = 10))
      .map(pipe)
      .take(10)
      .parJoin(10)

  //  val result = balancedStream.compile.toVector.unsafeRunSync
  //  println(result)
  balancedStream.compile.drain.unsafeRunSync()


  println("=====\n")
}
