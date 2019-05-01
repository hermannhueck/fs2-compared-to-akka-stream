package fs2guide.ch13zip

import cats.effect.{ContextShift, IO, Timer}

import scala.concurrent.duration._
import fs2.Stream

import scala.concurrent.ExecutionContext

object App03StreamZipLeft extends App {

  println("\n-----")

  val zippedLeft: List[Int] = Stream(1, 2, 3).zipLeft(Stream(4, 5, 6, 7)).toList
  println(zippedLeft)
  assert(zippedLeft == List(1, 2, 3))


  private val ec: ExecutionContext = scala.concurrent.ExecutionContext.Implicits.global
  implicit val cs: ContextShift[IO] = IO.contextShift(ec)
  implicit val timer: Timer[IO] = IO.timer(ec)

  val stream = Stream.range(0, 5) zipLeft Stream.fixedDelay(300.millis)
  val vec = stream.compile.toVector.unsafeRunSync
  println(vec)
  assert(vec == Vector(0, 1, 2, 3, 4))

  println("-----\n")
}
