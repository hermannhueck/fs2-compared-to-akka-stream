package fs2guide.ch17concurrrentFanout

import cats.effect.{ContextShift, IO}
import fs2.Stream

import scala.concurrent.ExecutionContext

object App12bStreamBalance extends App {

  println("\n-----")

  implicit val cs: ContextShift[IO] = IO.contextShift(ExecutionContext.global)

  val balancedStream: Stream[IO, Int] =
    Stream.range(0, 1000)
      .covary[IO]
      .prefetchN(100)
      .balance(chunkSize = 10)
      .map(_.evalMap { o =>
        IO { println(s">> adding 1000 to $o: " + (o + 1000)); o + 1000 }
      })
      .take(10)
      .parJoin(10)

  //  val result = balancedStream.compile.toVector.unsafeRunSync
  //  println(result)
  balancedStream.compile.drain.unsafeRunSync()

  println("-----\n")
}
