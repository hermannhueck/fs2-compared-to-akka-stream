package fs2guide.ch18interruptwhen

import cats.effect.concurrent.Deferred
import cats.effect.{ContextShift, IO, Timer}
import cats.syntax.flatMap._
import fs2.Stream
import cats.syntax.either._

import scala.concurrent.ExecutionContext
import scala.concurrent.duration._

object App05StreamInterruptWhen extends App {

  println("\n-----")

  val ec: ExecutionContext = ExecutionContext.global
  implicit val cs: ContextShift[IO] = IO.contextShift(ec)
  implicit val timer: Timer[IO] = IO.timer(ec)

  val stream: Stream[IO, Int] =
    Stream.range(1, 100)
      .zipLeft(Stream.awakeEvery[IO](250.milliseconds))
      .evalTap(i => IO(println(i)))

  def streamToInterrupt(interrupter: Deferred[IO, Either[Throwable, Unit]]): Stream[IO, Int] =
    stream.interruptWhen(interrupter)

  def interrupterStream(interrupter: Deferred[IO, Either[Throwable, Unit]]): Stream[IO, Unit] =
    Stream.sleep(2600.milliseconds) ++
      Stream.eval { IO(println("TIMEOUT")) >> interrupter.complete(().asRight[Throwable]) }

  val deferred: IO[Deferred[IO, Either[Throwable, Unit]]] = Deferred[IO, Either[Throwable, Unit]]

  val interruptedStream: Stream[IO, Int] =
    for {
      interrupter <- Stream.eval(deferred)
      int <- streamToInterrupt(interrupter) concurrently interrupterStream(interrupter)
    } yield int

  interruptedStream.compile.drain.unsafeRunSync()

  println("-----\n")
}
