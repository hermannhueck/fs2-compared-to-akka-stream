package factorial

import java.nio.file.Paths

import akka.actor.ActorSystem
import akka.stream.{ActorMaterializer, IOResult}
import akka.stream.scaladsl._
import akka.util.ByteString
import akka.{Done, NotUsed}

import scala.concurrent.duration._
import scala.concurrent.{Await, Future}


object Factorials1 extends App {

  implicit val system: ActorSystem = ActorSystem("Factorials1")
  implicit val materializer: ActorMaterializer = ActorMaterializer()

  val source: Source[Int, NotUsed] = Source(1 to 100)

  val factorials: Source[BigInt, NotUsed] = source.scan(BigInt(1))((acc, next) ⇒ acc * next)

  println("\n-----")

  val result: Future[IOResult] =
    factorials
      .map(num ⇒ ByteString(s"$num\n"))
      .runWith(FileIO.toPath(Paths.get("output/factorials.txt")))

  println(Await.result(result, 3.seconds))
  Await.result(system.terminate(), 3.seconds)

  println("-----\n")
}
