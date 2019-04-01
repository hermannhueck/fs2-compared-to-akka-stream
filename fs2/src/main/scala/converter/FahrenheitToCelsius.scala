package converter

import cats.effect.{ExitCode, IO, IOApp, Resource}
import cats.implicits._
import fs2.{io, text, Stream}
import java.nio.file.Paths
import java.util.concurrent.Executors
import scala.concurrent.ExecutionContext

/*
  Step-by-step explanation at:
  https://github.com/functional-streams-for-scala/fs2/blob/series/1.0/docs/src/ReadmeExample.md
  and at:
  https://www.youtube.com/watch?v=cahvyadYfX8
 */
object FahrenheitToCelsius extends IOApp {

  private val blockingExecutionContext =
    Resource
      .make(IO(ExecutionContext.fromExecutorService(Executors.newFixedThreadPool(2))))(ec => IO(ec.shutdown()))

  val converter: Stream[IO, Unit] = Stream.resource(blockingExecutionContext).flatMap { blockingEC =>

    def fahrenheitToCelsius(f: Double): Double =
      (f - 32.0) * (5.0/9.0)

    io.file.readAll[IO](Paths.get("testdata/fahrenheit.txt"), blockingEC, 4096)
      .through(text.utf8Decode)
      .through(text.lines)
      .filter(s => !s.trim.isEmpty && !s.startsWith("//"))
      .map(line => fahrenheitToCelsius(line.toDouble).toString)
      .intersperse("\n")
      .through(text.utf8Encode)
      .through(io.file.writeAll(Paths.get("output/celsius.txt"), blockingEC))
  }

  def run(args: List[String]): IO[ExitCode] =
    converter.compile.drain.as(ExitCode.Success)
}
