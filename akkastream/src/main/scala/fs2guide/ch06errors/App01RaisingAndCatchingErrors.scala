package fs2guide.ch06errors

import akka.{Done, NotUsed}
import akka.stream.scaladsl._
import akkautil.AkkaStreamApp

import scala.concurrent.{Await, Future}
import scala.concurrent.duration._
import scala.util.{Failure, Success}

object App01RaisingAndCatchingErrors extends AkkaStreamApp {

  val err0: Source[String, NotUsed] = Source.failed[String](new Exception("oh noes!"))

  val err1: Source[Int, NotUsed] = Source(1 to 3) ++ Source.failed(new Exception("!@#$"))

  // All these fail when running:

  val res0: Future[Done] = err0 runWith Sink.ignore
  res0 onComplete {
    case Success(result) => println(result)
    case Failure(exception) => println(exception.toString)
  }
  Thread sleep 500L

  println("-----")
  val res1: Future[Done] = err1 runWith Sink.foreach(println)
  res1 onComplete {
    case Success(result) => println(result)
    case Failure(exception) => println(exception.toString)
  }
  Thread sleep 500L

  println("-----")
  val res1b: Future[Seq[Int]] = err1 runWith Sink.seq
  res1b onComplete {
    case Success(result) => println(result)
    case Failure(exception) => println(exception.toString)
  }
  Thread sleep 500L

  println("-----")
  val res1c: Future[Seq[Int]] = err1 take 3 runWith Sink.seq
  res1c onComplete {
    case Success(result) => println(result)
    case Failure(exception) => println(exception.toString)
  }
  Thread sleep 500L


  Await.ready(system.terminate(), 3.seconds)
  println("=====\n")
}
