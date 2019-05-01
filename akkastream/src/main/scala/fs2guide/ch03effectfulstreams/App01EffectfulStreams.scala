package fs2guide.ch03effectfulstreams

import akka.stream.scaladsl.{Sink, Source}
import akka.{Done, NotUsed}
import akkautil.AkkaStreamApp

import scala.concurrent.{Await, Future}
import scala.concurrent.duration._

object App01EffectfulStreams extends AkkaStreamApp {

  // val eff: Stream[IO, Int] = Stream.eval(IO { println("TASK BEING RUN!!"); 1 + 1 })
  val effect: Source[Int, NotUsed] = Source
    .single { 1 + 1 }
    .mapMaterializedValue { _ => println("TASK BEING RUN!!"); NotUsed }

  val res1: Future[Seq[Int]] = effect.runWith(Sink.seq)
  res1 foreach println
  Thread sleep 200L

  val res2: Future[Done] = effect.runWith(Sink.ignore)
  res2 foreach println
  Thread sleep 200L

  val res3: Future[Int] = effect.runWith(Sink.head)
  res3 foreach println
  Thread sleep 200L

  val res4: Future[Int] = effect.runWith(Sink.fold(0)(_ + _))
  res4 foreach println
  Thread sleep 200L


  Await.ready(system.terminate(), 3.seconds)
  println("=====\n")
}
