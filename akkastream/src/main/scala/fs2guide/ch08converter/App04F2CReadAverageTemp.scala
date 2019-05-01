package fs2guide.ch08converter

import java.nio.file.{Path, Paths}

import akka.stream.IOResult
import akka.stream.scaladsl.{FileIO, Flow, Framing, Sink, Source}
import akka.util.ByteString
import akka.{Done, NotUsed}
import akkautil.AkkaStreamApp

import scala.concurrent.duration._
import scala.concurrent.{Await, Future}

object App04F2CReadAverageTemp extends AkkaStreamApp {

  val input: Path = Paths.get("testdata/fahrenheit.txt")

  def fahrenheitToCelsius(f: Double): Double =
    (f - 32.0) * (5.0 / 9.0)

  private val fileSource: Source[ByteString, Future[IOResult]] = FileIO.fromPath(input)

  val frameToLines: Flow[ByteString, String, NotUsed] =
    Framing.delimiter(ByteString("\n"), maximumFrameLength = 256, allowTruncation = true)
      .map(_.utf8String)

  val tempSource: Source[Option[Double], Future[IOResult]] =
    fileSource
      .via(frameToLines)
      .filter(s => !s.trim.isEmpty && !s.startsWith("//"))
      .map(line => fahrenheitToCelsius(line.toDouble))
      .fold((0.0: Double, 0L: Long)) {
        case ((accTemp, accCount), temperature) => (accTemp + temperature, accCount + 1)
      }
      .map {
        case (temperature, count) => if (count == 0L) None else Some(temperature / count)
      }

  val future: Future[Option[Double]] =
    tempSource runWith Sink.head

  val average: Option[Double] = Await.result(future, 1.second)
  val text = average.map(_.toString).getOrElse("no temperatures provided")
  println(s"\nAverage Temperature (Celsius):  $text\n")


  Await.ready(system.terminate(), 3.seconds)
  println("=====\n")
}
