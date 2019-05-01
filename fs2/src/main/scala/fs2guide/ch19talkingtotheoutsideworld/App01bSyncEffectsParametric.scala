package fs2guide.ch19talkingtotheoutsideworld

import cats.effect.{IO, Sync}
import fs2.Stream

import scala.language.higherKinds

object App01bSyncEffectsParametric extends App {

  println("\n-----")

  def destroyUniverse(): Unit = { println("BOOOOM!!!"); } // stub implementation

  // Stream.eval_
  // Creates a stream that evaluates the supplied fa for its effect, discarding the output value.

  def s[F[_]: Sync]: Stream[F, String] = Stream.eval_(Sync[F].delay { destroyUniverse() }) ++ Stream("...moving on")
  // def s[F[_]: Sync]: Stream[F, String] = Stream.eval_(Sync[F].delay { destroyUniverse() }) ++ Stream("...moving on")

  val res = s[IO].compile.toVector.unsafeRunSync()
  // BOOOOM!!!
  // res: Vector[String] = Vector(...moving on)

  println(res)

  println("-----\n")
}
