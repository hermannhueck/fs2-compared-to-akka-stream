package asdoc.tweets

import akka.stream.scaladsl._
import akka.{Done, NotUsed}
import akkautil.AkkaStreamApp

import scala.concurrent.duration._
import scala.concurrent.{Await, Future}


object App04UniqueHashtags extends AkkaStreamApp {

  import Tweets._

  val source: Source[String, NotUsed] =
    Source(tweets)
      .map(_.hashtags) // Get all sets of hashtags ...
      .reduce(_ ++ _) // ... and reduce them to a single set, removing duplicates across all tweets
      .mapConcat(identity) // Flatten the set of hashtags to a stream of hashtags
      .map(_.name.toUpperCase) // Convert all hashtags to upper case

  val result: Future[Done] =
    source
      .runWith(Sink foreach println) // Attach the Flow to a Sink that will finally print the hashtags

  Await.ready(result, 3.seconds)


  Await.result(system.terminate(), 3.seconds)
  println("=====\n")
}
