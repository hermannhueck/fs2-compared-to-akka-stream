package fs2guide.ch08converter

import java.nio.file.{Path, Paths}

import akka.NotUsed
import akka.stream.IOResult
import akka.stream.scaladsl.{FileIO, Flow, Framing, Keep, Sink, Source}
import akka.util.ByteString
import akkautil.AkkaStreamApp

import scala.concurrent.duration._
import scala.concurrent.{Await, Future}

object App05F2CReadWrite extends AkkaStreamApp {

  def fahrenheitToCelsius(f: Double): Double =
    (f - 32.0) * (5.0 / 9.0)

  val input: Path = Paths.get("testdata/fahrenheit.txt")
  val output = Paths.get("output/celsius-akka.txt")

  val fileSource: Source[ByteString, Future[IOResult]] = FileIO.fromPath(input)
  val fileSink: Sink[ByteString, Future[IOResult]] = FileIO.toPath(output)

  val frameToLines: Flow[ByteString, String, NotUsed] =
    Framing.delimiter(ByteString("\n"), maximumFrameLength = 256, allowTruncation = true)
      .map(_.utf8String)

  val byteStringSource =
    fileSource
      .via(frameToLines)
      .filter(s => !s.trim.isEmpty && !s.startsWith("//"))
      .map(line => fahrenheitToCelsius(line.toDouble).toString)
      .intersperse("\n")
      .map(str => ByteString(str))

  val (inResult, outResult) =
    byteStringSource
      .toMat(fileSink)(Keep.both)
      .run()

  println(Await.result(inResult, 1.second))
  println("-----")
  println(Await.result(outResult, 1.second))


  Await.ready(system.terminate(), 3.seconds)
  println("=====\n")
}
