package asdoc.tweets

import akka.Done
import akka.stream.OverflowStrategy
import akka.stream.scaladsl._
import akkautil.AkkaStreamApp

import scala.concurrent.duration._
import scala.concurrent.{Await, Future}


object App05TweetsBuffered extends AkkaStreamApp {

  import Tweets._

  def slowComputation(tweet: Tweet): Long = {
    Thread sleep 500L // act as if performing some heavy computation
    tweet.body.length
  }

  val result: Future[Done] =
    Source(tweets)
      .buffer(10, OverflowStrategy.dropHead)
      .map(slowComputation)
      .runWith(Sink.foreach(println))

  Await.ready(result, 10.seconds)


  Await.result(system.terminate(), 3.seconds)
  println("=====\n")
}
