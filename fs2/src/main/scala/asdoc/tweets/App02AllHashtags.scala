package asdoc.tweets

import cats.effect.IO
import fs2.Stream

import scala.language.higherKinds

object App02AllHashtags extends App {

  import Tweets._

  println("\n=====")

  val tweetSource: Stream[IO, Tweet] = Stream.emits(tweets).covary[IO]

  val allHashtags: Stream[IO, Hashtag] =
    tweetSource
      .flatMap(tweet => Stream.emits(tweet.hashtags.toList))

  val print: Stream[IO, Unit] =
    allHashtags
      .map(_.toString)
      .lines(java.lang.System.out)

  print.compile.drain.unsafeRunSync()


  println("=====\n")
}
