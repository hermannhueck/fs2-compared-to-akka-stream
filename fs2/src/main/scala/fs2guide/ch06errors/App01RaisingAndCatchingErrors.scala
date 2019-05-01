package fs2guide.ch06errors

import cats.effect.IO
import fs2.{INothing, Pure, Stream}

object App01RaisingAndCatchingErrors extends App {

  println("\n-----")

  // A stream can raise errors, either explicitly, using Stream.raiseError, or implicitly via an exception in pure code or inside an effect passed to eval:

  val err0: Stream[IO, INothing] = Stream.raiseError[IO](new Exception("oh noes!"))
  // err0: fs2.Stream[cats.effect.IO,fs2.INothing] = Stream(..)

  val err1: Stream[Pure, Int] = Stream(1,2,3) ++ (throw new Exception("!@#$"))
  // err1: fs2.Stream[[x]fs2.Pure[x],Int] = Stream(..)

  val err2: Stream[IO, Nothing] = Stream.eval(IO(throw new Exception("error in effect!!!")))
  // err2: fs2.Stream[cats.effect.IO,Nothing] = Stream(..)

  // All these fail when running:

  val res0 = try err0.compile.toList.unsafeRunSync catch { case e: Exception => println(e) }
  //=> java.lang.Exception: oh noes!
  //res0: Any = ()

  val res1 = try err1.toList catch { case e: Exception => println(e) }
  //=> java.lang.Exception: !@#$
  // res1: Any = ()

  val res2: Unit = try err2.compile.drain.unsafeRunSync() catch { case e: Exception => println(e) }
  //=> java.lang.Exception: error in effect!!!

  println("-----\n")
}
