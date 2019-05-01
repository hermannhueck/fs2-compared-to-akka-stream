package asdoc.tweets

import cats.effect.IO
import fs2.{Pipe, Stream}


object App01aTweetCount extends App {

  import Tweets._

  println("\n=====")

  val tweetSource: Stream[IO, Tweet] = Stream.emits(tweets).covary[IO]

  val mappingPipe: Pipe[IO, Tweet, Int] = { stream: Stream[IO, Tweet] =>
    stream.mapChunks { chunk => chunk map (_ => 1) }
  }

  val sum =
    tweetSource
      .through(mappingPipe)
      .fold(0)(_ + _)

  val ioList: IO[List[Int]] = sum.compile.toList
  val count = ioList.unsafeRunSync().head

  println(s"Total tweets processed: $count")


  println("=====\n")
}
