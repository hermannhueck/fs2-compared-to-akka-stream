package fs2guide.ch08factorial

import akka.stream.scaladsl._
import akka.{Done, NotUsed}
import akkautil.AkkaStreamApp

import scala.concurrent.duration._
import scala.concurrent.{Await, Future}


object Factorials2 extends AkkaStreamApp {

  val ints: Source[Int, NotUsed] = Source(1 to 30)
  val factorials: Source[BigInt, NotUsed] =
    ints.scan(BigInt(1))((acc, next) => acc * next)

  val result: Future[Done] =
    factorials
      .zipWithIndex
      .map { case (num, index) => s"$index = $num" }
      .throttle(1, 250.milliseconds)
      .runForeach(println)

  Await.ready(result, 6.seconds)


  Await.result(system.terminate(), 3.seconds)
  println("=====\n")
}
