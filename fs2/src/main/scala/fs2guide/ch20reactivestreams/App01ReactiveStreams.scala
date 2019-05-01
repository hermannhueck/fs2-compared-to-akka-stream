package fs2guide.ch20reactivestreams

import cats.effect.{ContextShift, IO}
import fs2.Stream
import fs2.interop.reactivestreams._

import scala.concurrent.ExecutionContext
import scala.language.higherKinds

object App01ReactiveStreams extends App {

  println("\n-----")

  implicit val contextShift: ContextShift[IO] = IO.contextShift(ExecutionContext.global)

  val stream: Stream[IO, Int] = Stream(1, 2, 3).covary[IO]
  // stream: fs2.Stream[cats.effect.IO,Int] = Stream(..)

  val publisher: StreamUnicastPublisher[IO, Int] = stream.toUnicastPublisher

  val stream2: Stream[IO, Int] = publisher.toStream[IO]

  stream2.compile.toList.map(println).unsafeRunSync()

  println("-----\n")
}
