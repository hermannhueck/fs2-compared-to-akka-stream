package asdoc.tweets

import cats.effect.IO
import fs2.Stream

import scala.language.higherKinds

object App01cTweetCount extends App {

  import Tweets._

  println("\n=====")

  def tweetSource[F[_]]: Stream[F, Tweet] = Stream.emits(tweets).covary[F]

  def sum[F[_]]: Stream[F, Int] =
    tweetSource
      .map(_ => 1)
      .fold(0)(_ + _)

  def ioList: IO[List[Int]] = sum[IO].compile.toList
  val count = ioList.unsafeRunSync().head

  println(s"Total tweets processed: $count")


  println("=====\n")
}
