package fs2guide.ch15concurrentFanin

import cats.effect.IO
import fs2.Stream

import scala.language.higherKinds

object App02StreamMergeHaltBoth extends App {

  println("\n-----")

  import cats.effect.ContextShift

  // This normally comes from IOApp
  implicit val ioContextShift: ContextShift[IO] = IO.contextShift(scala.concurrent.ExecutionContext.Implicits.global)
  // ioContextShift: cats.effect.ContextShift[cats.effect.IO] = cats.effect.internals.IOContextShift@eb64a2b

  val s1 = Stream(1, 2, 3).covary[IO]
  val s2 = Stream.eval(IO {Thread.sleep(200); 4})
  val s3 = Stream(4).covary[IO]

  // The merge function runs two streams concurrently, combining their outputs. It halts when either input has halted:
  val merged: Stream[IO, Int] = s1 mergeHaltBoth s2

  // ---- see also mergeHaltL and mergeHaltR

  val res = merged.compile.toVector.unsafeRunSync()
  // res: Vector[Int] = Vector(1, 2, 3)
  println(res)

  println("-----\n")
}
