package asdoc.tweets

import cats.effect.{ContextShift, IO}
import fs2.{Pipe, Stream}

import scala.concurrent.ExecutionContext
import scala.language.higherKinds

object App06TweetsBroadcast extends App {

  import Tweets._

  implicit val cs: ContextShift[IO] = IO.contextShift(ExecutionContext.global)

  println("\n=====")

  val tweetSource: Stream[IO, Tweet] = Stream.emits(tweets).covary[IO]

  val sinkAuthors: Pipe[IO, Tweet, Unit] = tweetStream =>
    tweetStream
      .map(_.author)
      .evalMap { author => IO(println(author)) }
      //.map(_.toString)
      //.lines(java.lang.System.out)

  val sinkHashtags: Pipe[IO, Tweet, Unit] = tweetStream =>
    tweetStream
      .flatMap(tweet => Stream.emits(tweet.hashtags.toList))
      .evalMap { hashtag => IO(println(hashtag)) }
      //.map(_.toString)
      //.lines(java.lang.System.out)

  tweetSource
    .broadcastThrough(sinkAuthors, sinkHashtags)
    .compile.drain.unsafeRunSync()


  println("=====\n")
}
