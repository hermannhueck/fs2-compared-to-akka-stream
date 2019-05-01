package fs2guide.ch11statefultransformation

import fs2.{Chunk, Pipe, Stream}

import scala.language.higherKinds

object App01StreamTake extends App {

  println("\n-----")


  implicit class exercise[+F[_], +O](stream: Stream[F, O]) {
    def myTake(n: Long): Stream[F, O] = stream.through(take(n))
  }


  // type Pipe[F[_], -I, +O] = Stream[F, I] => Stream[F, O]

  def take[F[_], O](n: Long): Pipe[F, O, O] = (in: Stream[F, O]) =>
    in.scanChunksOpt(n) { toTake: Long =>
      if (toTake <= 0) // don't fetch next Chunk
        None
      else { // fetch next Chunk
        val function: Chunk[O] => (Long, Chunk[O]) = chunk =>
          if (chunk.size < toTake)
            (toTake - chunk.size, chunk)
          else
            (0, chunk.take(toTake.toInt))
        Some(function)
      }
    }
  // take: [F[_], O](n: Long)fs2.Pipe[F,O,O]

  val myRes = Stream(1,2,3,4).myTake(2).toList
  // myRes: List[Int] = List(1, 2)
  println(myRes)

  val res = Stream(1,2,3,4).take(2).toList
  // res: List[Int] = List(1, 2)
  println(res)

  assert(myRes == res)

  println("-----\n")
}
