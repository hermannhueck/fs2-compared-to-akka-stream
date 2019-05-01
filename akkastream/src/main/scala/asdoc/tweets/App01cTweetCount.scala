package asdoc.tweets

import akka.stream.scaladsl._
import akkautil.AkkaStreamApp

import scala.concurrent.duration._
import scala.concurrent.{Await, Future}


object App01cTweetCount extends AkkaStreamApp {

  import Tweets._

  val sum: Future[Int] =
    Source(tweets)
      .map(_ => 1)
      .runFold(0)(_ + _)

  sum.foreach(count ⇒ println(s"Total tweets processed: $count"))


  Await.result(system.terminate(), 3.seconds)
  println("=====\n")
}
