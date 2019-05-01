package fs2guide.ch01streamtypes

import cats.effect.{IO, Sync}
import fs2.Stream
//import monix.eval.Task
//import monix.execution.Scheduler.Implicits.global
import myio.MyIO

import scala.language.higherKinds

object App05FStream extends App {

  println("\n----- Stream of generic effects")

  def stream[F[_]: Sync]: Stream[F, Int] = Stream.eval(Sync[F].delay {
    println("BEING RUN!!"); 1 + 1
  }).repeat.take(3)

  def effect[F[_]: Sync]: F[Vector[Int]] = stream.compile.toVector    // Up to this point nothing is run


  println(">>> reify F with cats.effect.IO:")
  val ioEffect: IO[Vector[Int]] = effect[IO]
  val ioResult: Vector[Int] = ioEffect.unsafeRunSync()                // produces side effects (println) and returns the result
  println(ioResult)


  println("\n>>> reify F with myio.MyIO:")
  val myioEffect: MyIO[Vector[Int]] = effect[MyIO]
  val myioResult: Vector[Int] = myioEffect.unsafeRunSync()                      // produces side effects (println) and returns the result
  println(myioResult)


/*
  println("\n>>> reify F with monix.eval.Task:")
  val taskEffect: Task[Vector[Int]] = effect[Task]
  val taskResult: Vector[Int] = taskEffect.runSyncUnsafe(3.seconds)    // produces side effects (println) and returns the result
  println(taskResult)
*/

  println("-----\n")
}
