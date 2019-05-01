package asdoc.cookbook

import cats.effect.IO
import fs2.{Stream, text}

object App08LinesFromByteString extends App {

  val rawData: Stream[IO, Byte] = Stream.emits(
    List(
      "Hello World",
      "\r",
      "!\r",
      "\nHello Akka!\r\nHello Streams!",
      "\r\n\r\n")).covary[IO]
    .through(text.utf8Encode)

  val linesStream = rawData
    .through(text.utf8Decode)
    .through(text.lines)

  val lines: Seq[String] = linesStream.compile.toVector.unsafeRunSync()
  println("-----")
  lines.map(_.toList) foreach println
  assert(lines == List("Hello World\r!", "Hello Akka!", "Hello Streams!", "", ""))


  println("=====\n")
}
