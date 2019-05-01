package asdoc.cookbook

import cats.effect.IO
import fs2.{Stream, compress, text}

object App09Compression extends App {

  println("\n=====")

  val compressedSource =
    Stream.emit("Hello World")
      .through(text.utf8Encode)
      .through(compress.gzip(100))
      .covary[IO]

  val uncompressedSource =
    compressedSource
      .through(compress.gunzip(100))
      .through(text.utf8Decode)

  val uncompressed: String = uncompressedSource.compile.toList.unsafeRunSync().head
  println(uncompressed)
  assert(uncompressed == "Hello World")


  println("=====\n")
}
