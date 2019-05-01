package asdoc.cookbook

import cats.effect.{ContextShift, IO, Timer}
import fs2.{Pure, Stream, compress, text}

import scala.concurrent.ExecutionContext
import scala.concurrent.duration._

object App12ManualTriggerZip extends App {

  println("\n=====")

  implicit val ec: ExecutionContext = ExecutionContext.global
  implicit val cs: ContextShift[IO] = IO.contextShift(ec)
  implicit val timer: Timer[IO] = IO.timer(ec)

  val elements: Stream[IO, String] = Stream.emits(List("1", "2", "3", "4", "5", "6")).covary[IO]

  val triggerSource1 = Stream.awakeEvery[IO](500.milliseconds)
  val triggerSource2 = Stream.awakeDelay[IO](500.milliseconds)
  val triggerSource3 = Stream.fixedDelay[IO](500.milliseconds)
  val triggerSource4 = Stream.fixedRate[IO](500.milliseconds)

  val triggerSource = triggerSource4

  val throttledSource = elements
    .zipLeft(triggerSource)
    .lines(java.lang.System.out)

  throttledSource.compile.drain.unsafeRunSync()


  println("=====\n")
}
