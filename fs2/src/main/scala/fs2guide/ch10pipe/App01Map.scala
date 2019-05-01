package fs2guide.ch10pipe

import fs2.{Chunk, Pipe, Stream}

import scala.language.higherKinds

object App01Map extends App {

  println("\n-----")

  // type Pipe[F[_], -I, +O] = Stream[F, I] => Stream[F, O]

  def mapPipe[F[_], I, O](f: I => O): Pipe[F, I, O] = { stream: Stream[F, I] =>
    stream.mapChunks { chunk: Chunk[I] => chunk map f }
  }

  implicit class exercise[+F[_], +I](stream: Stream[F, I]) {
    def myMap[O](f: I => O): Stream[F, O] = stream.through(mapPipe(f))
  }


  val stream = Stream.range(1, 11)

  val mySquares = stream.myMap(x => x * x).compile.toList
  println(mySquares)

  val squares = stream.myMap(x => x * x).compile.toList
  println(squares)

  assert(mySquares == squares)

  println("-----\n")
}
