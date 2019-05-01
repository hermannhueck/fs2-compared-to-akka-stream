package fs2guide.ch15concurrentFanin

import cats.effect.{IO, Timer}
import fs2.Stream

import scala.concurrent.duration._

import scala.language.higherKinds

object App03StreamEither extends App {

  println("\n-----")

  import cats.effect.ContextShift

  implicit val ioContextShift: ContextShift[IO] = IO.contextShift(scala.concurrent.ExecutionContext.Implicits.global)

  val s1 = Stream(1, 2, 3).covary[IO]
  val s2 = Stream.eval(IO {Thread.sleep(200); 4})

  val merged: Stream[IO, Either[Int, Int]] = s1 either s2

  val res1 = merged.compile.toVector.unsafeRunSync()
  // res: Vector[Int] = Vector(Left(1), Left(2), Left(3), Left(4))
  println(res1)
  assert(res1 == Vector(Left(1), Left(2), Left(3), Right(4)))
  Thread sleep 200L
  println("-----")


  implicit val timer: Timer[IO] = IO.timer(scala.concurrent.ExecutionContext.Implicits.global)

  val s3: Stream[IO, Int] = Stream.awakeEvery[IO](1000.millis).scan(0)((acc, i) => acc + 1)
  val s4: Stream[IO, Int] = (Stream.sleep_[IO](500.millis) ++ s3) take 10
  val s5: Stream[IO, Either[Int, Int]] = s3 either s4

  val res2: Vector[Either[Int, Int]] = s5.take(10).compile.toVector.unsafeRunSync
  println(res2)
  val expected = Vector(Left(0), Right(0), Left(1), Right(1), Left(2), Right(2), Left(3), Right(3), Left(4), Right(4))
  println(expected)
  assert(res2 == expected)

  println("-----\n")
}
