package fs2guide.ch14evalmap

import cats.effect.IO
import fs2.{Pure, Stream}

object App02StreamEvalMap extends App {

  println("\n-----")

  // ----- evalMap is an alias for flatMap(o => Stream.eval(f(o)))

  val s: Stream[IO, Int] = Stream(1, 2, 3, 4).covary[IO]

  s.flatMap(i => Stream.eval(IO(println(i)))).compile.drain.unsafeRunSync
  println("-----")
  s.evalMap(i => IO(println(i))).compile.drain.unsafeRunSync
  println("-----")

  val list1 = s.flatMap(i => Stream.eval(IO{println(i); i*i})).compile.toList.unsafeRunSync
  println(list1)
  println("-----")
  val list2 = s.evalMap(i => IO{println(i); i*i}).compile.toList.unsafeRunSync
  println(list2)

  println("-----\n")
}
