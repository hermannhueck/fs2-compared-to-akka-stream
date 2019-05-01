package asdoc.tweets

import akka.NotUsed
import akka.stream.scaladsl._
import akkautil.AkkaStreamApp

import scala.concurrent.duration._
import scala.concurrent.{Await, Future}


object App01aTweetCount extends AkkaStreamApp {

  import Tweets._

  val tweetSource: Source[Tweet, NotUsed] = Source(tweets)

  val count: Flow[Tweet, Int, NotUsed] = Flow[Tweet].map(_ => 1)

  val sumSink: Sink[Int, Future[Int]] = Sink.fold[Int, Int](0)(_ + _)

  val counterGraph: RunnableGraph[Future[Int]] =
    tweetSource
      .via(count)
      .toMat(sumSink)(Keep.right)

  val sum: Future[Int] = counterGraph.run()

  sum.foreach(count ⇒ println(s"Total tweets processed: $count"))


  Await.result(system.terminate(), 3.seconds)
  println("=====\n")
}
