package fs2guide.ch15concurrentFanin

import cats.effect.IO
import fs2.Stream

import scala.language.higherKinds

object App01StreamMerge extends App {

  println("\n-----")

  import cats.effect.ContextShift

  // This normally comes from IOApp
  implicit val ioContextShift: ContextShift[IO] = IO.contextShift(scala.concurrent.ExecutionContext.Implicits.global)
  // ioContextShift: cats.effect.ContextShift[cats.effect.IO] = cats.effect.internals.IOContextShift@eb64a2b

  val s1 = Stream(1, 2, 3).covary[IO]
  val s2 = Stream.eval(IO {Thread.sleep(200); 4})
  val s3 = Stream(4).covary[IO]

  val merged: Stream[IO, Int] = s1 merge s2

  val res1 = merged.compile.toVector.unsafeRunSync()
  // res: Vector[Int] = Vector(1, 2, 3, 4)
  println(res1)
  Thread sleep 200L

  val res2 = s2.merge(s1).compile.toVector.unsafeRunSync()
  // res: Vector[Int] = Vector(1, 2, 3, 4)
  println(res2)
  Thread sleep 200L

  val res3 = s3.merge(s1).compile.toVector.unsafeRunSync()
  // res: Vector[Int] = Vector(1, 2, 3, 4) or Vector(4, 1, 2, 3)
  println(res3)
  Thread sleep 200L

  println("-----\n")
}
