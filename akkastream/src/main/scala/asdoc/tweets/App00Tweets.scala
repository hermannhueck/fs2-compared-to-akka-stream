package asdoc.tweets

import akka.stream.scaladsl._
import akka.{Done, NotUsed}
import akkautil.AkkaStreamApp

import scala.concurrent.duration._
import scala.concurrent.{Await, Future}


object App00Tweets extends AkkaStreamApp {

  import Tweets._

  val done: Future[Done] =
    Source(tweets) runWith Sink.foreach(println)

  Await.ready(done, 3.seconds)


  Await.result(system.terminate(), 3.seconds)
  println("=====\n")
}
