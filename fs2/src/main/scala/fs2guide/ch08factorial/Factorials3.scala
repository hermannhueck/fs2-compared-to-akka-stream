package fs2guide.ch08factorial

import java.nio.file.{Paths, StandardOpenOption}

import cats.effect.{ContextShift, IO}
import fs2.{Stream, io, text}

import scala.concurrent.ExecutionContext


object Factorials3 extends App {

  println("\n=====")

  val ec: ExecutionContext = ExecutionContext.global
  implicit val cs: ContextShift[IO] = IO.contextShift(ec)

  val ints: Stream[IO, Int] = Stream.range(1, 31).covary[IO]
  val factorials: Stream[IO, BigInt] =
    ints.scan(BigInt(1))((acc, next) => acc * next)

  val stream: Stream[IO, Unit] =
    factorials
      .zipWithIndex
      .map { case (num, index) => s"$index = $num\n" }
      .through(text.utf8Encode)
      .through(io.file.writeAll(
        Paths.get("output/factorials-fs2.txt"),
        ec,
        flags = List(StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING))
      )

  stream.compile.drain.unsafeRunSync()


  println("=====\n")
}
