package asdoc.cookbook

import akka.stream.scaladsl.{Sink, Source}
import akka.util.ByteString
import akkautil.AkkaStreamApp

import scala.concurrent.Await
import scala.concurrent.duration._

object App08LinesFromByteString extends AkkaStreamApp {

  val rawData = Source(
    List(
      ByteString("Hello World"),
      ByteString("\r"),
      ByteString("!\r"),
      ByteString("\nHello Akka!\r\nHello Streams!"),
      ByteString("\r\n\r\n")))

  import akka.stream.scaladsl.Framing
  val linesStream = rawData
    .via(Framing.delimiter(ByteString("\r\n"), maximumFrameLength = 100, allowTruncation = true))
    .map(_.utf8String)

  val lines: Seq[String] = Await.result(linesStream.limit(10).runWith(Sink.seq), 3.seconds)
  assert(lines == List("Hello World\r!", "Hello Akka!", "Hello Streams!", ""))
  lines foreach println


  Await.ready(system.terminate(), 3.seconds)
  println("=====\n")
}
