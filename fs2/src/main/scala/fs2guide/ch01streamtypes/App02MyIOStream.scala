package fs2guide.ch01streamtypes

import fs2.Stream
import myio.MyIO

object App02MyIOStream extends App {

  println("\n----- Stream of IO effects")

  val stream: Stream[MyIO, Int] = Stream.eval(MyIO.eval {
    println("BEING RUN!!"); 1 + 1
  }).repeat.take(3)

  val effect: MyIO[Vector[Int]] = stream.compile.toVector           // Up to this point nothing is run

  val result: Vector[Int] = effect.unsafeRunSync()   // produces side effects (println) and returns the result

  println(result)

  println("-----\n")
}
