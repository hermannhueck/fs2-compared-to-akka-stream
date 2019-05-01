package fs2guide.ch05chunks

import cats.effect.IO
import fs2.{Chunk, Stream}

import scala.language.higherKinds

object App02HidingChunks extends App {

  println("\n----- Hiding the Chunks of a Stream")

  def printAsList[A](s: Stream[IO, A]): Unit = println(s.compile.toList.unsafeRunSync())

  val stream: Stream[IO, Int] = Stream.range(0, 16).covary[IO]

  println("\n// 1. stream: Stream[IO, Int]")
  printAsList(stream)
  printAsList(stream.chunks)


  // hide chunks with Stream#cons ... reverses the chunk order
  //
  def unchunkReversed[F[_], O](chunkStream: Stream[F, Chunk[O]]): Stream[F, O] = {
    val empty: Stream[F, O] = Stream.empty.covary[F]
    chunkStream.fold(empty)((stream, chunk) => stream cons chunk).flatten // cons reverses the Stream of Chunks
  }

  println("\n// 2. unchunkedReversed: Stream[IO, Chunk[Int]]")
  private val chunked = stream.chunkN(4)
  val unchunkedReversed: Stream[IO, Int] = unchunkReversed(chunked)
  printAsList(unchunkedReversed)

  println("\n// unchunkedReversed.chunks: Stream[IO, Int]")
  printAsList(unchunkedReversed.chunks)


  // hide chunks with Stream#++ ... maintains the chunk order
  //
  def unchunk[F[_], O](chunkStream: Stream[F, Chunk[O]]): Stream[F, O] = {
    val empty: Stream[F, O] = Stream.empty.covary[F]
    chunkStream.fold(empty)((stream, chunk) => stream ++ Stream.chunk(chunk)).flatten // ++ maintains the order of Chunks
  }

  println("\n// 3. unchunked: Stream[IO, Chunk[Int]]")
  val unchunked = unchunk(stream.chunkN(4))
  printAsList(unchunked)

  println("\n// 4. unchunked.chunks: Stream[IO, Int]")
  printAsList(unchunked.chunks)

  println("-----\n")
}
