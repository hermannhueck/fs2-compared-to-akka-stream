package fs2guide.ch01streamtypes

import cats.effect.IO
import fs2.Stream

object App01IOStream extends App {

  println("\n----- Stream of IO effects")

  val stream: Stream[IO, Int] = Stream.eval(IO {
    println("BEING RUN!!"); 1 + 1
  }).repeat.take(3)

  val effect: IO[Vector[Int]] = stream.compile.toVector   // Up to this point nothing is run

  val result: Vector[Int] = effect.unsafeRunSync()    // produces side effects (println) and returns the result

  println(result)

  println("-----\n")
}
