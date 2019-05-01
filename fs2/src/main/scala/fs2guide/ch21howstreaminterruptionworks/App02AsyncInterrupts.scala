package fs2guide.ch21howstreaminterruptionworks

import cats.effect.{ContextShift, IO}
import fs2.{INothing, Stream}

import scala.concurrent.ExecutionContext
import scala.language.higherKinds

object App02AsyncInterrupts extends App {

  println("\n-----")

  implicit val contextShift: ContextShift[IO] = IO.contextShift(ExecutionContext.global)

  case object Err extends Throwable

  val s1: Stream[IO, Int] = (Stream(1) ++ Stream(2)).covary[IO]

  val s2: Stream[IO, INothing] = (Stream.empty ++ Stream.raiseError[IO](Err)).handleErrorWith { e => println(e); Stream.raiseError[IO](e) }

  val merged: Stream[IO, Int] = s1 merge s2 take 1

  val res03: Vector[Int] = merged.compile.toVector.unsafeRunSync()
  println(res03)

  // The result is highly nondeterministic. Here are a few ways it can play out:
  //
  // - s1 may complete before the error in s2 is encountered, in which case nothing will be printed and no error will occur.
  // - s2 may encounter the error before any of s1 is emitted. When the error is reraised by s2,
  //   that will terminate the merge and asynchronously interrupt s1, and the take terminates with that same error.
  // - s2 may encounter the error before any of s1 is emitted, but during the period where the value is caught by handleErrorWith,
  //   s1 may emit a value and the take(1) may terminate, triggering interruption of both s1 and s2,
  //   before the error is reraised but after the exception is printed! In this case, the stream will still terminate without error.
  //
  // The correctness of your program should not depend on how different streams interleave, and once again,
  // you should not use handleErrorWith or other interruptible functions for resource cleanup. Use bracket or onFinalize for this purpose.
  
  println("-----\n")
}
