package fs2guide.ch12exercise2pull

import fs2.{Chunk, INothing, Pipe, Pull, Stream}

import scala.language.higherKinds

object App01StreamTakeWhileWithoutPull extends App {

  println("\n-----")


  implicit class exercise[+F[_], +O](stream: Stream[F, O]) {
    def myTakeWhile(predicate: O => Boolean): Stream[F, O] = stream.through(takeWhile(predicate))
  }



  def takeWhile[F[_], O](predicate: O => Boolean): Pipe[F, O, O] = { in: Stream[F, O] =>
    type State = Boolean
    in.scanChunksOpt(true) { takeMore: State =>
      if (!takeMore)
        None
      else {
        val function: Chunk[O] => (State, Chunk[O]) = chunk => {
          val newChunk: Chunk[O] = Chunk.vector(chunk.toVector.takeWhile(predicate))
          (newChunk.size == chunk.size, newChunk)
        }
        Some(function)
      }
    }
  }

  val myRes = Stream.range(0, 100).through(takeWhile(_ < 7)).toList
  // myRes: List[Int] = List(0, 1, 2, 3, 4, 5, 6)
  println(myRes)

  val res = Stream.range(0, 100).takeWhile(_ < 7).toList
  // res: List[Int] = List(0, 1, 2, 3, 4, 5, 6)
  println(res)

  assert(myRes == res)

  println("-----\n")
}
