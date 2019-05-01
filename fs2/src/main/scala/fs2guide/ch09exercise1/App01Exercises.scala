package fs2guide.ch09exercise1

import cats.effect.IO
import fs2.{INothing, Stream}

import scala.language.higherKinds

object App01Exercises extends App {

  println("\n-----")


  implicit class exercises[+F[_], +O](s: Stream[F, O]) {
    def repeatIt(): Stream[F, O] = s ++ s.repeatIt()
    def drainIt(): Stream[F, INothing] = s >> Stream.empty
    def attemptIt(): Stream[F, Either[Throwable, O]] = s.map(o => Right(o)).handleErrorWith(t => Stream(Left(t)))
  }


  def exEval_[F[_], O](fa: F[O]): Stream[F, INothing] = Stream.eval(fa) >> Stream.empty

  println("\n>>> repeat:")
  val repeated:   List[Int] = Stream(1,0).repeat.take(6).toList
  println(repeated)
  val exRepeated: List[Int] = Stream(1,0).repeatIt().take(6).toList
  println(exRepeated)
  assert(repeated == exRepeated)

  println("\n>>> drain:")
  val drained:   List[INothing] = Stream(1,2,3).drain.toList
  println(drained)
  val exDrained: List[INothing] = Stream(1,2,3).drainIt().toList
  println(exDrained)
  assert(drained == exDrained)

  println("\n>>> eval_:")
  val evaluated_ :   Vector[INothing] = Stream.eval_(IO(println("!!"))).compile.toVector.unsafeRunSync()
  println(evaluated_)
  val exEvaluated_ : Vector[INothing] = exEval_(IO(println("!!"))).compile.toVector.unsafeRunSync()
  println(exEvaluated_)
  assert(evaluated_ == exEvaluated_)

  println("\n>>> attempt:")
  val attempted:   List[Either[Throwable, Int]] = (Stream(1,2) ++ (throw new Exception("nooo!!!"))).attempt.toList
  println(attempted)
  val exAttempted: List[Either[Throwable, Int]] = (Stream(1,2) ++ (throw new Exception("nooo!!!"))).attemptIt().toList
  println(exAttempted)
  // assert(attempted == exAttempted) // not equal! why?

  println("-----\n")
}
