package fs2guide.ch05chunks

import cats.effect.IO
import fs2.Stream

object App01RevealingChunks extends App {

  println("\n----- Revealing the Chunks of a Stream")

  def printAsList[A](s: Stream[IO, A]): Unit = println(s.compile.toList.unsafeRunSync())

  val stream: Stream[IO, Int] = Stream.range(0, 16).covary[IO]

  println("\n// 1. stream: Stream[IO, Int]")
  printAsList(stream)

  println("\n// 2. stream.chunks: Stream[IO, Chunk[Int]]")
  printAsList(stream.chunks)

  println("\n// 3. stream.chunkN(4): Stream[IO, Chunk[Int]]")
  printAsList(stream.chunkN(4))

  println("\n// 4. stream.chunkLimit(4): Stream[IO, Chunk[Int]]")
  printAsList(stream.chunkLimit(4))

  println("-----\n")
}
