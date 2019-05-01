package fs2guide.ch12exercise2pull

import fs2.{Chunk, INothing, Pipe, Pull, Stream}

import scala.language.higherKinds

object App01StreamTakeWhile extends App {

  println("\n-----")


  implicit class exercise[+F[_], +O](stream: Stream[F, O]) {
    def myTakeWhile(predicate: O => Boolean): Stream[F, O] = stream.through(takeWhile(predicate))
  }


  def takeWhile[F[_], O](predicate: O => Boolean): Pipe[F, O, O] = {

    def go(stream: Stream[F, O], p: O => Boolean): Pull[F, O, Unit] = {

      val pull: Pull[F, INothing, Option[(Chunk[O], Stream[F, O])]] = stream.pull.uncons

      pull.flatMap {
        case None => Pull.done
        case Some((headChunk, tailStream)) =>
          val vec: Vector[O] = headChunk.toVector.takeWhile(p)
          if (headChunk.size - vec.size > 0)
              Pull.output(Chunk.vector(vec)) >> Pull.done
            else
              Pull.output(headChunk) >> go(tailStream, p)
      }
    }

    in => go(in, predicate).stream
  }

  val myRes = Stream.range(0,100).through(takeWhile(_ < 7)).toList
  // myRes: List[Int] = List(0, 1, 2, 3, 4, 5, 6)
  println(myRes)

  val res = Stream.range(0,100).takeWhile(_ < 7).toList
  // res: List[Int] = List(0, 1, 2, 3, 4, 5, 6)
  println(res)

  assert(myRes == res)

  println("-----\n")
}
