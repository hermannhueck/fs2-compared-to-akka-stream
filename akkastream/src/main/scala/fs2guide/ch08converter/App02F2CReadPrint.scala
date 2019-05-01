package fs2guide.ch08converter

import java.nio.file.{Path, Paths}

import akka.{Done, NotUsed}
import akka.stream.IOResult
import akka.stream.scaladsl.{FileIO, Flow, Framing, Keep, Sink, Source}
import akka.util.ByteString
import akkautil.AkkaStreamApp

import scala.concurrent.duration._
import scala.concurrent.{Await, Future}

object App02F2CReadPrint extends AkkaStreamApp {

  val input: Path = Paths.get("testdata/fahrenheit.txt")

  def fahrenheitToCelsius(f: Double): Double =
    (f - 32.0) * (5.0 / 9.0)

  private val fileSource: Source[ByteString, Future[IOResult]] = FileIO.fromPath(input)

  val frameToLines: Flow[ByteString, String, NotUsed] =
    Framing.delimiter(ByteString("\n"), maximumFrameLength = 256, allowTruncation = true)
      .map(_.utf8String)

  val tempSource: Source[String, Future[IOResult]] =
    fileSource
      .via(frameToLines)
      .filter(s => !s.trim.isEmpty && !s.startsWith("//"))
      .map(line => fahrenheitToCelsius(line.toDouble).toString)

  val done: Future[Done] =
    tempSource runWith (Sink foreach println)

  Await.ready(done, 1.second)


  Await.ready(system.terminate(), 3.seconds)
  println("=====\n")
}
