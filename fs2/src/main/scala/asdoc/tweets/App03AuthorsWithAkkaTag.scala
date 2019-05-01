package asdoc.tweets

import cats.effect.IO
import fs2.Stream

import scala.language.higherKinds

object App03AuthorsWithAkkaTag extends App {

  import Tweets._

  println("\n=====")

  val tweetSource: Stream[IO, Tweet] = Stream.emits(tweets).covary[IO]

  val authors: Stream[IO, Author] =
    tweetSource
      .filter(_.hashtags.contains(akkaTag))
      .map(_.author)

  val print: Stream[IO, Unit] =
    authors
      .map(_.toString)
      .lines(java.lang.System.out)

  print.compile.drain.unsafeRunSync()


  println("=====\n")
}
