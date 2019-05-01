package fs2guide.ch08factorial

import java.nio.file.Paths

import akka.NotUsed
import akka.stream.IOResult
import akka.stream.scaladsl._
import akka.util.ByteString
import akkautil.AkkaStreamApp

import scala.concurrent.duration._
import scala.concurrent.{Await, Future}


object Factorials3 extends AkkaStreamApp {

  val ints: Source[Int, NotUsed] = Source(1 to 30)
  val factorials: Source[BigInt, NotUsed] =
    ints.scan(BigInt(1))((acc, next) => acc * next)

  val result: Future[IOResult] =
    factorials
      .zipWithIndex
      .map { case (num, index) => s"$index = $num\n" }
      .map(ByteString(_))
      .runWith(FileIO.toPath(Paths.get("output/factorials-akka.txt")))

  println(Await.result(result, 3.seconds))


  Await.result(system.terminate(), 3.seconds)
  println("=====\n")
}
