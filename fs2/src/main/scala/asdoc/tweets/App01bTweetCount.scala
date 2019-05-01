package asdoc.tweets

import cats.effect.IO
import fs2.{Pipe, Stream}


object App01bTweetCount extends App {

  import Tweets._

  println("\n=====")

  val tweetSource: Stream[IO, Tweet] = Stream.emits(tweets).covary[IO]

  val sum =
    tweetSource
      .map(_ => 1)
      .fold(0)(_ + _)

  val ioList: IO[List[Int]] = sum.compile.toList
  val count = ioList.unsafeRunSync().head

  println(s"Total tweets processed: $count")


  println("=====\n")
}
