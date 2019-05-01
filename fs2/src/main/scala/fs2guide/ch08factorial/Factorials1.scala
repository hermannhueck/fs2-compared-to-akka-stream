package fs2guide.ch08factorial

import cats.effect.IO
import fs2.Stream


object Factorials1 extends App {

  println("\n=====")

  val ints: Stream[IO, Int] = Stream.range(1, 31).covary[IO]
  val factorials: Stream[IO, BigInt] =
    ints.scan(BigInt(1))((acc, next) => acc * next)

  val stream: Stream[IO, Unit] =
    factorials
      .map(_.toString)
      .lines(java.lang.System.out)

  stream.compile.drain.unsafeRunSync()


  println("=====\n")
}
