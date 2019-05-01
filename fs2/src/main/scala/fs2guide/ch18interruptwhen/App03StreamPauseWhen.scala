package fs2guide.ch18interruptwhen

import cats.effect.{ContextShift, IO, Timer}
import fs2.Stream

import scala.concurrent.ExecutionContext
import scala.concurrent.duration._

object App03StreamPauseWhen extends App {

  println("\n-----")

  val ec: ExecutionContext = ExecutionContext.global
  implicit val cs: ContextShift[IO] = IO.contextShift(ec)
  implicit val timer: Timer[IO] = IO.timer(ec)

  val stream: Stream[IO, Int] =
    Stream.range(1, 30)
      .zipLeft(Stream.awakeEvery[IO](250.milliseconds))
      .evalTap(i => IO(println(i)))

  val pauser = Stream.sleep(2600.milliseconds) >> Stream.eval(IO(println("PAUSE"))) >> Stream.eval(IO(true))
  val resumer = Stream.sleep(2600.milliseconds) >> Stream.eval(IO(println("RESUME"))) >> Stream.eval(IO(false))

  val pauserStream: Stream[IO, Boolean] = pauser ++ resumer

  val pauseedStream: Stream[IO, Int] =
    stream.pauseWhen(pauserStream) // !!!!! Doesn't work! Don't know why!

  pauseedStream.compile.drain.unsafeRunSync()

  println("-----\n")
}
