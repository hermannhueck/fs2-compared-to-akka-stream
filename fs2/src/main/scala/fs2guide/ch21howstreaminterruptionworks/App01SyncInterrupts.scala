package fs2guide.ch21howstreaminterruptionworks

import cats.effect.IO
import fs2.Stream

object App01SyncInterrupts extends App {

  println("\n-----")

  case object Err extends Throwable

  val res01: List[Int] = (Stream(1) ++ (throw Err)).take(1).toList
  println(res01)

  // The take 1 uses Pull but doesn’t examine the entire stream, and neither of these examples will ever throw an error.
  // This makes sense. A bit more subtle is that this code will also never throw an error:

  val res02: List[Int] = (Stream(1) ++ Stream.raiseError[IO](Err)).take(1).compile.toList.unsafeRunSync()
  println(res02)

  // The reason is simple: the consumer (the take(1)) terminates as soon as it has an element. Once it has that element,
  // it is done consuming the stream and doesn’t bother running any further steps of it,
  // so the stream never actually completes normally—it has been interrupted before that can occur.
  // We may be able to see in this case that nothing follows the emitted 1,
  // but FS2 doesn’t know this until it actually runs another step of the stream.
  //
  // If instead we use onFinalize, the code is guaranteed to run, regardless of whether take interrupts:

  val res03: Vector[Int] =
    Stream(1).covary[IO].
      onFinalize(IO { println("finalized!") }).
      take(1).
      compile.toVector.unsafeRunSync()
  // finalized!
  // res03: Vector[Int] = Vector(1)
  println(res03)

  println("-----\n")
}
