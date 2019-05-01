package fs2guide.ch04streamoperations

import akka.stream.scaladsl.{Sink, Source}
import akka.{Done, NotUsed}
import akkautil.AkkaStreamApp
import fs2guide.ch03effectfulstreams.App01EffectfulStreams.system

import scala.concurrent.{Await, Future}
import scala.concurrent.duration._

object App01StreamOperations extends AkkaStreamApp {

  println(">>> Source#concat <> Stream#append")
  val res1: Future[Seq[Int]] = Source(Seq(1, 2, 3)).concat(Source(Seq(4, 5))).runWith(Sink.seq)
  println(Await.result(res1, 1.second)) //==> Vector(1, 2, 3, 4, 5)

  println(">>> Source#++ <> Stream#++ == alias for append/concat")
  val res2: Future[Seq[Int]] = Source(Seq(1, 2, 3)).++(Source(Seq(4, 5))).runWith(Sink.seq)
  println(Await.result(res2, 1.second)) //==> Vector(1, 2, 3, 4, 5)

  println(">>> Source#map <> Stream#map")
  val res3: Future[Seq[Int]] = Source(Seq(1, 2, 3)).map(_ + 1).runWith(Sink.seq)
  println(Await.result(res3, 1.second)) //==> Vector(2, 3, 4)

  println(">>> Source#filter <> Stream#filter")
  val res4: Future[Seq[Int]] = Source(Seq(1, 2, 3)).filter(_ % 2 != 0).runWith(Sink.seq)
  println(Await.result(res4, 1.second)) //==> Vector(1, 3)

  println(">>> Source#fold <> Stream#fold")
  val res5 = Source(Seq(1, 2, 3)).fold(0)(_ + _).runWith(Sink.seq)
  println(Await.result(res5, 1.second)) //==> Vector(6)

  println(">>> Source#collect <> Stream#collect")
  val res6 = Source(Seq(None, Some(2), Some(3))).collect { case Some(i) => i }.runWith(Sink.seq)
  println(Await.result(res6, 1.second)) //==> Vector(2, 3)

  println(">>> Source#intersperse <> Stream#intersperse")
  val res7 = Source((0 until 5).toSeq).intersperse(42).runWith(Sink.seq)
  println(Await.result(res7, 1.second)) //==> Vector(0, 42, 1, 42, 2, 42, 3, 42, 4)

  println(">>> Source#mapConcat/flatMapConcat/flatMapMerge <> Stream#flatMap")
  val res8a = Source(1 to 3).mapConcat(i => Seq(i, i)).runWith(Sink.seq)
  println(Await.result(res8a, 1.second)) //==> Vector(1, 1, 2, 2, 3, 3)
  val res8b = Source(1 to 3).flatMapConcat(i => Source(Seq(i, i))).runWith(Sink.seq)
  println(Await.result(res8b, 1.second)) //==> Vector(1, 1, 2, 2, 3, 3)
  val res8c = Source(1 to 3).flatMapMerge(breadth = 2, i => Source(Seq(i, i))).runWith(Sink.seq)
  println(Await.result(res8c, 1.second)) //==> Vector(2, 1, 2, 1, 3, 3) // with nondeterministic order

  println(">>> Source.repeat, Source#mapConcat, Source#take <> Stream#repeat")
  val res9 = Source.repeat(1 to 3).mapConcat(identity).take(10).runWith(Sink.seq)
  println(Await.result(res9, 1.second)) //==> Vector(1, 2, 3, 1, 2, 3, 1, 2, 3, 1)

  println(">>> Source.repeat, Source#take, Source#mapConcat <> Stream#repeatN")
  val res10 = Source.repeat(1 to 3).take(2).mapConcat(identity).runWith(Sink.seq)
  println(Await.result(res10, 1.second)) //==> Vector(1, 2, 3, 1, 2, 3)


  Await.ready(system.terminate(), 3.seconds)
  println("=====\n")
}
