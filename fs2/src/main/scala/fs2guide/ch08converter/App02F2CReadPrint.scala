package fs2guide.ch08converter

import java.nio.file.{Path, Paths}

import cats.effect.{ContextShift, IO}
import fs2.{Stream, io, text}

import scala.concurrent.ExecutionContext

object App02F2CReadPrint extends App {

  private val ec: ExecutionContext = ExecutionContext.global
  private implicit val cs: ContextShift[IO] = IO.contextShift(ec)

  private val input: Path = Paths.get("testdata/fahrenheit.txt")

  def fahrenheitToCelsius(f: Double): Double =
    (f - 32.0) * (5.0 / 9.0)

  val converter: Stream[IO, Unit] =
    io.file.readAll[IO](input, ec, 4096)
      .through(text.utf8Decode)
      .through(text.lines)
      .filter(s => !s.trim.isEmpty && !s.startsWith("//"))
      .map(line => fahrenheitToCelsius(line.toDouble).toString)
      // .map { line => println(line); line }
      .lines(java.lang.System.out)

  // this also works for large files which don't fit into memory

  val ioUnit: IO[Unit] = converter.compile.drain
  ioUnit.unsafeRunSync()
}
