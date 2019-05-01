package asdoc.tweets

import akka.stream.scaladsl._
import akka.{Done, NotUsed}
import akkautil.AkkaStreamApp

import scala.concurrent.duration._
import scala.concurrent.{Await, Future}


object App03AuthorsWithAkkaTag extends AkkaStreamApp {

  import Tweets._

  val authors: Source[Author, NotUsed] =
    Source(tweets)
      .filter(_.hashtags.contains(akkaTag))
      .map(_.author)

  val result: Future[Done] =
    authors runWith Sink.foreach(println)

  Await.ready(result, 3.seconds)


  Await.result(system.terminate(), 3.seconds)
  println("=====\n")
}
