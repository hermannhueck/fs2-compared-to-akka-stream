package fs2guide.ch09exercise1

import akka.NotUsed
import akka.actor.ActorSystem
import akka.stream.scaladsl.{FileIO, Flow, Framing, Keep, Sink, Source}
import akka.stream.{ActorMaterializer, IOResult}
import akka.util.ByteString
import akkautil.AkkaStreamApp
import com.typesafe.config.ConfigFactory

import scala.concurrent.duration._
import scala.concurrent.{Await, ExecutionContext, Future}
import scala.language.higherKinds

object App01Exercises extends AkkaStreamApp {


  implicit class exercises[+Out, +Mat](s: Source[Out, Mat]) {
    def repeatIt(): Source[Out, Mat] = s ++ s.repeatIt() // !!! produces a StackOverflowError
    def drainIt(): Source[Out, Mat] = s mapConcat { _ => Seq.empty[Out] }
    def drainIt2(): Source[Out, Mat] = s flatMapConcat { _ => Source.empty[Out] }
    def attemptIt(): Source[Either[Throwable, Out], Mat] =
      s.map(o => Right(o)).recoverWithRetries(1, {
        case t: Throwable => Source.single[Either[Throwable, Out]](Left(t))
      })
  }


  //def exEval_[F[_], O](fa: F[O]): Stream[F, INothing] = Stream.eval(fa) >> Stream.empty

/*
  println("\n>>> repeat:")
  val fRepeated: Future[Seq[Int]] = Source.repeat(1 to 2).take(3).mapConcat(identity).runWith(Sink.seq)
  val repeated = Await.result(fRepeated, 1.second)
  println(repeated)
  val fExRepeated: Future[Seq[Int]] = Source(1 to 2).repeatIt().take(6).runWith(Sink.seq)
  val exRepeated = Await.result(fExRepeated, 1.second)
  println(exRepeated)
  assert(repeated == exRepeated)
*/

  println("\n>>> drain:")
  val fDrained = Source(1 to 3).mapConcat { _ => Seq.empty }.runWith(Sink.seq)
  val drained = Await.result(fDrained, 1.second)
  println(drained)
  val fExDrained = Source(1 to 3).drainIt().runWith(Sink.seq)
  val exDrained = Await.result(fExDrained, 1.second)
  println(exDrained)
  assert(drained == exDrained)

  println("\n>>> eval_:")
  val fEvaluated_ = Source.single(println("!!")).runWith(Sink.seq)
  val evaluated_ = Await.result(fEvaluated_, 1.second)
  println(evaluated_)

  println("\n>>> attempt:")
  val fExAttempted: Future[Seq[Either[Throwable, String]]] = Source(0 to 6).map(n =>
    if (n < 5) n.toString
    else throw new RuntimeException("Boom!")
  ).attemptIt().runWith(Sink.seq)
  val exAttempted = Await.result(fExAttempted, 1.second)
  println(exAttempted)


  Await.ready(system.terminate(), 3.seconds)
  println("=====\n")
}
