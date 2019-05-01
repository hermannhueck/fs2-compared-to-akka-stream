package fs2guide.ch15concurrentFanin

import cats.effect.IO
import fs2.Stream
import fs2.concurrent.SignallingRef

import scala.language.higherKinds

object App05StreamConcurrently extends App {

  println("\n-----")

  import cats.effect.ContextShift

  implicit val cs: ContextShift[IO] = IO.contextShift(scala.concurrent.ExecutionContext.Implicits.global)

  val data: Stream[IO, Int] = Stream.range(1, 10).covary[IO]

  val signal: IO[SignallingRef[IO, Int]] = fs2.concurrent.SignallingRef[IO, Int](0)

  val intStream: Stream[IO, Int] = Stream.eval(signal).flatMap { sigRef =>
    Stream(sigRef) concurrently data.evalMap(value => sigRef.set(value))
  }.flatMap { sig =>
    sig.discrete  // stream of the updates to this signal
  }.takeWhile(_ < 9, takeFailure = true)

  val result: Option[Int] = intStream.compile.last.unsafeRunSync
  println(result)
  assert(result.contains(9))

  println("-----\n")
}
