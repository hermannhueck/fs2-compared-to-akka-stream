package fs2guide.ch08converter

import java.nio.file.{Path, Paths}

import cats.effect.{ExitCode, IO, IOApp}
import cats.syntax.functor._
import fs2.{Stream, io, text}

import scala.concurrent.ExecutionContext

object App05F2CReadWrite extends IOApp {

  val input: Path = Paths.get("testdata/fahrenheit.txt")
  val output: Path = Paths.get("output/celsius-fs2.txt")

  val ec: ExecutionContext = ExecutionContext.global

  def fahrenheitToCelsius(f: Double): Double =
    (f - 32.0) * (5.0/9.0)

  val converter: Stream[IO, Unit] =
    io.file.readAll[IO](input, ec, 4096)
      .through(text.utf8Decode)
      .through(text.lines)
      .filter(s => !s.trim.isEmpty && !s.startsWith("//"))
      .map(line => fahrenheitToCelsius(line.toDouble).toString)
      // .map { line => println(line); line }
      .intersperse("\n")
      .through(text.utf8Encode)
      .through(io.file.writeAll(output, ec))

  def run(args: List[String]): IO[ExitCode] =
    converter.compile.drain.as(ExitCode.Success)
}
