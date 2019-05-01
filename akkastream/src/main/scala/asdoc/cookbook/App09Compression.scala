package asdoc.cookbook

import akka.NotUsed
import akka.stream.scaladsl.{Sink, Source}
import akka.util.ByteString
import akkautil.AkkaStreamApp

import scala.concurrent.Await
import scala.concurrent.duration._

object App09Compression extends AkkaStreamApp {

  import akka.stream.scaladsl.Compression

  val compressedSource: Source[ByteString, NotUsed] =
    Source.single(ByteString.fromString("Hello World"))
      .via(Compression.gzip)

  val uncompressedSource: Source[String, NotUsed] =
    compressedSource
      .via(Compression.gunzip())
      .map(_.utf8String)

  val uncompressed: String = Await.result(uncompressedSource.runWith(Sink.head), 3.seconds)
  println(uncompressed)
  assert(uncompressed == "Hello World")


  Await.ready(system.terminate(), 3.seconds)
  println("=====\n")
}
