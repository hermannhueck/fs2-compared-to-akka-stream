package fs2guide.ch18interruptwhen

import cats.effect.{ContextShift, IO, Timer}
import cats.syntax.flatMap._
import fs2.Stream
import fs2.concurrent.{Signal, SignallingRef}

import scala.concurrent.ExecutionContext
import scala.concurrent.duration._

object App04StreamPauseWhen extends App {

  println("\n-----")

  val ec: ExecutionContext = ExecutionContext.global
  implicit val cs: ContextShift[IO] = IO.contextShift(ec)
  implicit val timer: Timer[IO] = IO.timer(ec)

  val stream: Stream[IO, Int] =
    Stream.range(1, 30)
      .zipLeft(Stream.awakeEvery[IO](250.milliseconds))
      .evalTap(i => IO(println(i)))

  def streamToPause(interrupter: Signal[IO, Boolean]): Stream[IO, Int] =
    stream.pauseWhen(interrupter)

  def pauserStream(interrupter: SignallingRef[IO, Boolean]): Stream[IO, Unit] =
    Stream.sleep(2600.milliseconds) ++
      Stream.eval { IO(println("PAUSE")) >> interrupter.set(true) } ++
      Stream.sleep(2600.milliseconds) ++
      Stream.eval { IO(println("RESUME")) >> interrupter.set(false) }

  val sigref: IO[SignallingRef[IO, Boolean]] = SignallingRef[IO, Boolean](initial = false)

  val pausedStream: Stream[IO, Int] =
    for {
      interrupter <- Stream.eval(sigref)
      int <- streamToPause(interrupter) concurrently pauserStream(interrupter)
    } yield int

  pausedStream.compile.drain.unsafeRunSync()

  println("-----\n")
}
