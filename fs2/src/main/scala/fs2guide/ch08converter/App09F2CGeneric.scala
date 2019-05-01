package fs2guide.ch08converter

import java.nio.file.{Path, Paths}
import java.util.concurrent.Executors

import cats.effect.{ContextShift, ExitCode, IO, IOApp, Sync}
import cats.syntax.functor._
import fs2.{Stream, io, text}

import scala.concurrent.{ExecutionContext, ExecutionContextExecutorService}

import scala.language.higherKinds

/*
  Step-by-step explanation at:
  https://github.com/functional-streams-for-scala/fs2/blob/series/1.0/docs/src/ReadmeExample.md
  and at:
  https://www.youtube.com/watch?v=cahvyadYfX8
 */
object App09F2CGeneric extends IOApp {

  val input: Path = Paths.get("testdata/fahrenheit.txt")
  val output: Path = Paths.get("output/celsius-fs2.txt")

  def blockingExecutionContext: ExecutionContextExecutorService =
    ExecutionContext.fromExecutorService(Executors.newFixedThreadPool(2))

  def fahrenheitToCelsius(f: Double): Double =
    (f - 32.0) * (5.0 / 9.0)

  def convert[F[_]: Sync: ContextShift](ec: ExecutionContext): Stream[F, Unit] =
    io.file.readAll(input, ec, 4096)
      .through(text.utf8Decode)
      .through(text.lines)
      .filter(s => !s.trim.isEmpty && !s.startsWith("//"))
      .map(line => fahrenheitToCelsius(line.toDouble).toString)
      // .map { line => println(line); line }
      .intersperse("\n")
      .through(text.utf8Encode)
      .through(io.file.writeAll(output, ec)) // ++ Stream.eval[IO, Unit](IO { throw new IllegalStateException("illegal state")} )

  def converter[F[_]: Sync: ContextShift]: Stream[F, Unit] =
    Stream.bracket(Sync[F].delay(blockingExecutionContext))(ec => Sync[F].delay(ec.shutdown()))
    .flatMap { ec => convert(ec) }

  def run(args: List[String]): IO[ExitCode] =
    converter[IO].compile.drain.as {
      println(s"\nFahrenheit from $input converted to Celsius in $output\n")
      ExitCode.Success
    }
}
