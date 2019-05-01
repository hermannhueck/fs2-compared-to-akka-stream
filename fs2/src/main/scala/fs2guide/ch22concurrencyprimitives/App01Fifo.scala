package fs2guide.ch22concurrencyprimitives

import cats.implicits._
import cats.effect.{Concurrent, ExitCode, IO, IOApp, Timer}
import fs2.concurrent.Queue
import fs2.Stream

import scala.concurrent.duration._

import scala.language.higherKinds

class Buffering[F[_]](q1: Queue[F, Int], q2: Queue[F, Int])(implicit F: Concurrent[F]) {

  def start: Stream[F, Unit] =
    Stream(
      // writes 1000 Ints into q1
      Stream.range(0, 1000).covary[F].through(q1.enqueue),
      // dequeues from q1 and enques into q2
      q1.dequeue.through(q2.enqueue),
      // dequeues from q2 and performs side effect for each value dequeued
      //.map won't work here as you're trying to map a pure value with a side effect. Use `evalMap` instead.
      q2.dequeue.evalMap(n => F.delay(println(s"Pulling out $n from Queue #2")))
    ).parJoin(3)
}

object App01Fifo extends IOApp {

  override def run(args: List[String]): IO[ExitCode] = {
    val stream: Stream[IO, Unit] = for {
      q1 <- Stream.eval(Queue.bounded[IO, Int](1))
      q2 <- Stream.eval(Queue.bounded[IO, Int](100))
      buffering = new Buffering[IO](q1, q2)
      _  <- Stream.sleep_[IO](5.seconds) concurrently buffering.start.drain
    } yield ()
    stream.compile.drain.as(ExitCode.Success)
  }
}
