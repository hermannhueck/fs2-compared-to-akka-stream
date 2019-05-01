package fs2guide.ch06errors

import akka.NotUsed
import akka.stream.scaladsl._
import akkautil.AkkaStreamApp

import scala.concurrent.Await
import scala.concurrent.duration._

object App02HandlingErrors extends AkkaStreamApp {

  val errStream: Source[String, NotUsed] = Source(0 to 6).map(n =>
    if (n < 5) n.toString
    else throw new RuntimeException("Boom!")
  )


  println("\n>>> errStream.recover")
  errStream.recover {
    case e: RuntimeException => s"stream truncated due to ${e.getClass.getSimpleName} with message ${e.getMessage}"
  }.runForeach(println)
  Thread sleep 500L


  println("\n>>> errStream.recoverWithRetries")
  errStream.recoverWithRetries(attempts = 1, {
    case e: RuntimeException => Source(List("five", "six", "seven", "eight"))
  }).runForeach(println)
  Thread sleep 500L


  Await.ready(system.terminate(), 3.seconds)
  println("=====\n")
}
