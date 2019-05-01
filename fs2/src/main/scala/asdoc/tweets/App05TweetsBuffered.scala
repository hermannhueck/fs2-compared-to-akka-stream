package asdoc.tweets

import cats.effect.IO
import fs2.Stream

import scala.language.higherKinds

object App05TweetsBuffered extends App {

  import Tweets._

  println("\n=====")

  def slowComputation(tweet: Tweet): Long = {
    Thread sleep 500L // act as if performing some heavy computation
    tweet.body.length
  }

  val tweetSource: Stream[IO, Tweet] = Stream.emits(tweets).covary[IO]

  val allHashtags: Stream[IO, Long] =
    tweetSource
      .buffer(10) // behaves different from Akka Stream
      .map(slowComputation)

  val print: Stream[IO, Unit] =
    allHashtags
      .map(_.toString)
      .lines(java.lang.System.out)

  print.compile.drain.unsafeRunSync()


  println("=====\n")
}
