package fs2guide.ch12exercise2pull

import fs2.{Chunk, INothing, Pipe, Pull, Stream}

import scala.language.higherKinds

object App02StreamIntersperseWithoutPull extends App {

  println("\n-----")


  implicit class exercise[+F[_], O](stream: Stream[F, O]) {
    def myIntersperse(separator: O): Stream[F, O] = stream.through(intersperse(separator))
  }


  // type Pipe[F[_], -I, +O] = Stream[F, I] => Stream[F, O]

  def intersperseImpl01[F[_], O](separator: O): Pipe[F, O, O] = { in: Stream[F, O] =>
    type State = O
    in.scanChunksOpt(separator) { sep: State =>
      val function: Chunk[O] => (State, Chunk[O]) = chunk => (sep, chunk.flatMap(o => Chunk(sep, o)))
      Some(function)
    }.tail // remove leading separator
  }

  def intersperseImpl02[F[_], O](separator: O): Pipe[F, O, O] = { in: Stream[F, O] =>
    type State = Unit // we don't really need a state for intersperse
    in.scanChunksOpt(()) { _: State =>
      val function: Chunk[O] => (State, Chunk[O]) = chunk => ((), chunk.flatMap(o => Chunk(separator, o)))
      Some(function)
    }.tail // remove leading separator
  }

  def intersperseImpl03[F[_], O](separator: O): Pipe[F, O, O] = { in: Stream[F, O] =>
    type State = Unit // we don't really need a state for intersperse
    in.scanChunks(()) { // scanChunks is sufficient, as we scan all Chunks
      val function: (State, Chunk[O]) => (State, Chunk[O]) = (state, chunk) => ((), chunk.flatMap(o => Chunk(separator, o)))
      function
    }.tail // remove leading separator
  }

  def intersperseImpl04[F[_], O](separator: O): Pipe[F, O, O] = in =>
    in
      .flatMap { elem => Stream(separator, elem) }
      .tail // remove leading separator

  def intersperseImpl05[F[_], O](separator: O): Pipe[F, O, O] =
    _.flatMap { Stream(_, separator) }.dropLast // remove trailing separator

  def intersperse[F[_], O](separator: O): Pipe[F, O, O] = intersperseImpl05(separator)

  val myRes = Stream("Alice","Bob","Carol").myIntersperse("|").toList
  // myRes: List[String] = List(Alice, |, Bob, |, Carol)
  println(myRes)

  val res = Stream("Alice","Bob","Carol").intersperse("|").toList
  // res: List[String] = List(Alice, |, Bob, |, Carol)
  println(res)

  assert(myRes == res)

  println("-----\n")
}
