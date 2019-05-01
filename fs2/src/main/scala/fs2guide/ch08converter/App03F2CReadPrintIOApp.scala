package fs2guide.ch08converter

import java.nio.file.{Path, Paths}

import cats.effect.{ContextShift, ExitCode, IO, IOApp}
import fs2.{Stream, io, text}

import scala.concurrent.ExecutionContext

object App03F2CReadPrintIOApp extends IOApp {

  private val input: Path = Paths.get("testdata/fahrenheit.txt")

  def fahrenheitToCelsius(f: Double): Double =
    (f - 32.0) * (5.0 / 9.0)

  val converter: Stream[IO, Unit] =
    io.file.readAll[IO](input, ExecutionContext.global, 4096)
      .through(text.utf8Decode)
      .through(text.lines)
      .filter(s => !s.trim.isEmpty && !s.startsWith("//"))
      .map(line => fahrenheitToCelsius(line.toDouble).toString)
      .lines(java.lang.System.out)

  override def run(args: List[String]): IO[ExitCode] = {
    converter.compile.drain.unsafeRunSync()
    IO(ExitCode.Success)
  }
}
