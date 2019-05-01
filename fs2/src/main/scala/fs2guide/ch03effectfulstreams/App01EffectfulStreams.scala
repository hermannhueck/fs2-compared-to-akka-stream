package fs2guide.ch03effectfulstreams

import cats.effect.IO
import fs2.Stream

object App01EffectfulStreams extends App {

  println("\n-----")

  val stream: Stream[IO, Int] = Stream.eval(IO { println("TASK BEING RUN!!"); 1 + 1 })

  /*
  // Effectful Stream cannot be converted to a List or Vector without preceding compilation.

  scala> stream.toList
  <console>:16: error: value toList is not a member of fs2.Stream[cats.effect.IO,Int]
       eff.toList
           ^
   */

  // The first .compile.toVector is one of several methods available to ‘compile’ the stream to a single effect:
  
  val io1 = stream.compile.toVector // gather all output into a Vector
  // ra: cats.effect.IO[Vector[Int]] = <function1>

  val io2 = stream.compile.drain // purely for effects (result ignored)
  // rb: cats.effect.IO[Unit] = <function1>

  val io3 = stream.compile.fold(0)(_ + _) // run and accumulate some result
  // rc: cats.effect.IO[Int] = <function1>


  val res1 = io1.unsafeRunSync()
  //=> TASK BEING RUN!!
  // res1: Vector[Int] = Vector(2)
  println(res1)

  val res2: Unit = io2.unsafeRunSync()
  //=> TASK BEING RUN!!
  // res2: Unit = ()
  println(res2)

  val res3a = io3.unsafeRunSync()
  //=> TASK BEING RUN!!
  // res3a: Int = 2
  println(res3a)

  println("// No memoization!")
  println("// The 2nd run produces the side effect and the result again.")
  val res3b = io3.unsafeRunSync()
  //=> TASK BEING RUN!!
  // res3b: Int = 2
  println(res3b)
  
  println("-----\n")
}
