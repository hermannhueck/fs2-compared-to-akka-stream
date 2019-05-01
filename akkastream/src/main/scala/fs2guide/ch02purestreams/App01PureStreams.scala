package fs2guide.ch02purestreams

import akka.NotUsed
import akka.stream.scaladsl.{Sink, Source}
import akkautil.AkkaStreamApp

import scala.concurrent.{Await, Future}
import scala.concurrent.duration._

object App01PureStreams extends AkkaStreamApp {

  println("--- define an empty stream")
  // val s0: Stream[Pure, INothing] = Stream.empty
  val s0: Source[Nothing, NotUsed] = Source.empty

  println("--- define a stream emitting 1 element")
  // val s1: Stream[Pure, Int] = Stream.emit(1)
  val s1: Source[Int, NotUsed] = Source.single(1)

  println("--- define a stream emitting a sequence of elements (a range in this case)")
  // val s1a: Stream[Pure, Int] = Stream(1, 2, 3) // variadic
  val s1a = Source(1 to 3)

  println("--- define a stream emitting a sequence of elements")
  // val s1b: Stream[Pure, Int] = Stream.emits(List(4, 5, 6)) // accepts any Seq
  val s1b = Source(Seq(4, 5, 6))


  println("--- convert stream in to a Seq, List or Vector")
  // You can convert a pure stream to a List or Vector using:
  // s1.toList
  // s1.toVector
  val fSeq: Future[Seq[Int]] = s1.runWith(Sink.seq)
  println(Await.result(fSeq, 1.second))
  Thread sleep 100L

  // ‘list-like’ functions

  println("--- appending 2 streams")
  val s2: Source[Int, NotUsed] = s1a ++ s1b
  val seq2: Seq[Int] = Await.result(s2.runWith(Sink.seq), 1.second)
  println(seq2)
  // (Stream(1, 2, 3) ++ Stream(4, 5)).toList
  Thread sleep 100L

  println("--- mapping a stream")
  val s3: Source[Int, NotUsed] = Source(1 to 3).map(_ + 1)
  s3 runWith Sink.seq foreach println
  // Stream(1, 2, 3).map(_ + 1).toList
  Thread sleep 100L

  println("--- filtering a stream")
  val s4: Source[Int, NotUsed] = Source(1 to 3).filter(_ % 2 != 0)
  s4 runWith Sink.seq foreach println
  // Stream(1, 2, 3).filter(_ % 2 != 0).toList
  Thread sleep 100L

  println("--- folding a stream")
  val fInt1: Future[Int] = s1a.runWith(Sink.fold(0)(_ + _))
  fInt1 foreach println
  val fInt2: Future[Int] = s1a.runFold(0)(_ + _)
  fInt2 foreach println
  // Stream(1, 2, 3).fold(0)(_ + _).toList
  Thread sleep 100L

  println("--- collecting elements of a stream")
  val s5 = Source(Seq(None, Some(2), Some(3))).collect { case Some(i) => i }
  val seq5: Seq[Int] = Await.result(s5.runWith(Sink.seq), 1.second)
  println(seq5)
  // Stream(None, Some(2), Some(3)).collect { case Some(i) => i }.toList
  Thread sleep 100L

  println("--- interspersing elements into a stream")
  val s6 = Source(0 to 5).intersperse(42)
  val seq6: Seq[Int] = Await.result(s6.runWith(Sink.seq), 1.second)
  println(seq6)
  // Stream.range(0, 5).intersperse(42).toList
  Thread sleep 100L

  println("--- flatMapping a stream")
  // flatMap
  // Stream(1, 2, 3).flatMap(i => Stream(i, i)).toList
  // intentionally no 'flatMap' operation in akka-stream
  // mapConcat or mapMerge instead, which have different semantics:
  Source(1 to 3).mapConcat(i => Seq(i, i)) runWith Sink.seq foreach println
  Thread sleep 100L
  Source(1 to 3).flatMapConcat(i => Source(Seq(i, i))) runWith Sink.seq foreach println
  Thread sleep 100L
  Source(1 to 3).flatMapMerge(breadth = 2, i => Source(Seq(i, i))) runWith Sink.seq foreach println
  Thread sleep 200L

  println("--- repeating stream elements")
  // Stream(1,2,3).repeatN(2).toList
  Source.repeat(1 to 3).take(2).mapConcat(identity) runWith Sink.seq foreach println
  Source.repeat(1 to 3).mapConcat(identity).take(6) runWith Sink.seq foreach println
  Thread sleep 200L


  Await.ready(system.terminate(), 3.seconds)
  println("=====\n")
}
