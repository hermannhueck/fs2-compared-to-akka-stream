package fs2guide.ch18interruptwhen

import cats.effect.{ContextShift, IO, Timer}
import cats.syntax.flatMap._
import fs2.Stream
import fs2.concurrent.{Signal, SignallingRef}

import scala.concurrent.ExecutionContext
import scala.concurrent.duration._

object App04StreamInterruptWhen extends App {

  println("\n-----")

  val ec: ExecutionContext = ExecutionContext.global
  implicit val cs: ContextShift[IO] = IO.contextShift(ec)
  implicit val timer: Timer[IO] = IO.timer(ec)

  val stream: Stream[IO, Int] =
    Stream.range(1, 100)
      .zipLeft(Stream.awakeEvery[IO](250.milliseconds))
      .evalTap(i => IO(println(i)))

  def streamToInterrupt(interrupter: Signal[IO, Boolean]): Stream[IO, Int] =
    stream.interruptWhen(interrupter)

  def interrupterStream(interrupter: SignallingRef[IO, Boolean]): Stream[IO, Unit] =
    Stream.sleep(2600.milliseconds) ++
      Stream.eval { IO(println("TIMEOUT")) >> interrupter.set(true) }

  val sigref: IO[SignallingRef[IO, Boolean]] = SignallingRef[IO, Boolean](initial = false)

  val interruptedStream: Stream[IO, Int] =
    for {
      interrupter <- Stream.eval(sigref)
      int <- streamToInterrupt(interrupter) concurrently interrupterStream(interrupter)
    } yield int

  interruptedStream.compile.drain.unsafeRunSync()

  println("-----\n")
}
