package asdoc.tweets

import cats.effect.IO
import fs2.Stream

import scala.language.higherKinds

object App04UniqueHashtags extends App {

  import Tweets._

  println("\n=====")

  val tweetSource: Stream[IO, Tweet] = Stream.emits(tweets).covary[IO]

  val uniqueHashtags =
    tweetSource
      .map(_.hashtags) // Get all sets of hashtags ...
      .reduce(_ ++ _) // ... and reduce them to a single set, removing duplicates across all tweets
      .flatMap(set => Stream.emits(set.toList)) // Flatten the set of hashtags to a stream of hashtags
      .map(_.name.toUpperCase) // Convert all hashtags to upper case

  val print: Stream[IO, Unit] =
    uniqueHashtags
      .lines(java.lang.System.out)

  print.compile.drain.unsafeRunSync()


  println("=====\n")
}
