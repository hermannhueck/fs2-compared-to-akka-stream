package fs2guide.ch02purestreams

import fs2.{INothing, Pure, Stream}

object App01PureStreams extends App {

  println("\n-----")

  // creates an empty pure Stream
  val s0: Stream[Pure, INothing] = Stream.empty
  // s0: fs2.Stream[fs2.Pure,fs2.INothing] = Stream(..)
  println(s0.toList)

  // creates a singleton Stream with exactly one pure value
  val s1: Stream[Pure, Int] = Stream.emit(1)
  // s1: fs2.Stream[[x]fs2.Pure[x],Int] = Stream(..)
  println(s1.toList)

  // creates a singleton Stream with exactly one pure value
  val s1Bad: Stream[Pure, Int] = Stream.emit { println("Side effect in a pure Stream! ...   OH NO!!!   This is not referentially transparent!"); 1}
  // s1Bad: fs2.Stream[[x]fs2.Pure[x],Int] = Stream(..)
  Thread sleep 2000L
  println(s1Bad.toList)

  // creates a Stream from a Seq of pure values
  val s2: Stream[Pure, Int] = Stream.emits(List(1, 2, 3))
  // s2: fs2.Stream[[x]fs2.Pure[x],Int] = Stream(..)
  println(s2.toList)

  // creates pure Stream from a variable number of pure arguments
  val s3: Stream[Pure, Int] = Stream(1, 2, 3)
  // s3: fs2.Stream[[x]fs2.Pure[x],Int] = Stream(..)
  println(s3.toList)

  // creates pure Stream of Int from a range
  val s4a: Stream[Pure, Int] = Stream.range(0, 20)
  // s4a: fs2.Stream[[x]fs2.Pure[x],Int] = Stream(..)
  println(s4a.toList)

  val s4b: Stream[Pure, Int] = Stream.range(0, 20, 2)
  // s4b: fs2.Stream[[x]fs2.Pure[x],Int] = Stream(..)
  println(s4b.toList)

  // creates infinite Stream of pure values
  // by applying the given function to the start value and the subsequent values
  val s5: Stream[Pure, Int] = Stream.iterate(0)(_ + 2)
  // s5: fs2.Stream[[x]fs2.Pure[x],Int] = Stream(..)
  println(s5.take(10).toList)

  println("-----\n")
}
