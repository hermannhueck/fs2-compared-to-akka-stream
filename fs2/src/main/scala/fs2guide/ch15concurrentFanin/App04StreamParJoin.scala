package fs2guide.ch15concurrentFanin

import cats.effect.IO
import fs2.{Pure, Stream}

import scala.language.higherKinds

object App04StreamParJoin extends App {

  println("\n-----")

  import cats.effect.ContextShift

  // This normally comes from IOApp
  implicit val ioContextShift: ContextShift[IO] = IO.contextShift(scala.concurrent.ExecutionContext.Implicits.global)
  // ioContextShift: cats.effect.ContextShift[cats.effect.IO] = cats.effect.internals.IOContextShift@eb64a2b

  val s = Stream.range(0, 5).covary[IO]
  val s1 = s map {"A" + _.toString}
  val s2 = s map {"B" + _.toString}
  val s3 = s map {"C" + _.toString}
  val s4 = s map {"D" + _.toString}
  val s5 = s map {"E" + _.toString}

  val streamOfStreams: Stream[IO, Stream[IO, String]] = Stream(s1, s2, s3, s4, s5).covary[IO]

  // The parJoin function runs maxOpen streams concurrently, combining their outputs nondeterministically.
  val joined = streamOfStreams.parJoin(3)
  val resJoined = joined.compile.toVector.unsafeRunSync()
  println(resJoined)

  // The parJoinUnbounded function runs an unbounded number of streams concurrently, combining their outputs nondeterministically.
  val joinedUnbounded = streamOfStreams.parJoinUnbounded
  val resJoinedUnbounded = joinedUnbounded.compile.toVector.unsafeRunSync()
  println(resJoinedUnbounded)

  println("-----\n")
}
