package asdoc.tweets

import akka.stream.scaladsl._
import akka.{Done, NotUsed}
import akkautil.AkkaStreamApp

import scala.concurrent.duration._
import scala.concurrent.{Await, Future}


object App02AllHashtags extends AkkaStreamApp {

  import Tweets._

  val hashtags: Source[Hashtag, NotUsed] =
    Source(tweets)
      .mapConcat(_.hashtags.toList)

  val result: Future[Done] =
    hashtags runWith Sink.foreach(println)

  Await.ready(result, 3.seconds)


  Await.result(system.terminate(), 3.seconds)
  println("=====\n")
}
