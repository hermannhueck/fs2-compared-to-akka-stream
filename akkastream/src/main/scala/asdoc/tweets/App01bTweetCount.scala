package asdoc.tweets

import akka.stream.scaladsl._
import akkautil.AkkaStreamApp

import scala.concurrent.duration._
import scala.concurrent.{Await, Future}


object App01bTweetCount extends AkkaStreamApp {

  import Tweets._

  val sum: Future[Int] =
    Source(tweets)
      .via(Flow[Tweet].map(_ => 1))
      .toMat(Sink.fold(0)(_ + _))(Keep.right)
      .run()

  sum.foreach(count ⇒ println(s"Total tweets processed: $count"))


  Await.result(system.terminate(), 3.seconds)
  println("=====\n")
}
