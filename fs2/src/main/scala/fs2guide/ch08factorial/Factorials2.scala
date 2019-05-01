package fs2guide.ch08factorial

import cats.effect.{IO, Timer}
import fs2.Stream

import scala.concurrent.duration._
import scala.concurrent.{ExecutionContext, ExecutionContextExecutor}


object Factorials2 extends App {

  println("\n=====")

  val ec: ExecutionContextExecutor = ExecutionContext.global
  implicit val timer: Timer[IO] = IO.timer(ec)

  val ints: Stream[IO, Int] = Stream.range(1, 31).covary[IO]
  val factorials: Stream[IO, BigInt] =
    ints.scan(BigInt(1))((acc, next) => acc * next)

  val stream: Stream[IO, Unit] =
    factorials
      .zipWithIndex
      .map { case (num, index) => s"$index = $num" }
      .zipLeft(Stream.fixedRate[IO](250.millis))
      .lines(java.lang.System.out)

  stream.compile.drain.unsafeRunSync()


  println("=====\n")
}
