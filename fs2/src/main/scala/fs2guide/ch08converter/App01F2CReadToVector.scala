package fs2guide.ch08converter

import java.nio.file.{Path, Paths}

import cats.effect.{ContextShift, ExitCode, IO, IOApp}
import fs2.{Stream, io, text}

import scala.concurrent.ExecutionContext

object App01F2CReadToVector extends App {

  private val ec: ExecutionContext = ExecutionContext.global
  private implicit val cs: ContextShift[IO] = IO.contextShift(ec)

  private val input: Path = Paths.get("testdata/fahrenheit.txt")

  def fahrenheitToCelsius(f: Double): Double =
    (f - 32.0) * (5.0 / 9.0)

  val converter: Stream[IO, String] =
    io.file.readAll[IO](input, ec, 4096)
      .through(text.utf8Decode)
      .through(text.lines)
      .filter(s => !s.trim.isEmpty && !s.startsWith("//"))
      .map(line => fahrenheitToCelsius(line.toDouble).toString)

    val ioVector: IO[Vector[String]] = converter.compile.toVector
    ioVector.unsafeRunSync() foreach println
}
