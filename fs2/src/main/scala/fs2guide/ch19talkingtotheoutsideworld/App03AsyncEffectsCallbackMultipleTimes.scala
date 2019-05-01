package fs2guide.ch19talkingtotheoutsideworld

import cats.effect.{ConcurrentEffect, ContextShift, IO}
import fs2.Stream
import fs2.concurrent.Queue

import scala.concurrent.ExecutionContext
import scala.language.higherKinds

object App03AsyncEffectsCallbackMultipleTimes extends App {

  println("\n-----")

  type Row = List[String]

  trait CSVHandle {
    def withRows(cb: Either[Throwable,Row] => Unit): Unit
  }

  def rows[F[_]](handle: CSVHandle)(implicit F: ConcurrentEffect[F], cs: ContextShift[F]): Stream[F,Row] =
    for {
      q <- Stream.eval(Queue.unbounded[F, Either[Throwable, Row]])
      _ <- Stream.eval { F.delay(handle.withRows(e => F.runAsync(q.enqueue1(e))(_ => IO.unit).unsafeRunSync)) }
      row <- q.dequeue.rethrow
    } yield row

  val csvHandle: CSVHandle = new CSVHandle {
    override def withRows(cb: Either[Throwable, Row] => Unit): Unit = Right(List("these", "are", "the", "cells", "of", "a", "CSV", "row" ))
  }

  implicit val cs: ContextShift[IO] = IO.contextShift(ExecutionContext.global)

  val stream: Stream[IO, Row] = rows[IO](csvHandle)

  val io: IO[List[Row]] = stream.compile.toList
  val res: List[Row] = io.unsafeRunSync()

  println(res)

  println("-----\n")
}
