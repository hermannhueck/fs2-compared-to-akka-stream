package fs2guide.ch16exercise3

import cats.effect.{Concurrent, IO}
import fs2.{Chunk, Pipe, Pipe2, Stream}

import scala.language.higherKinds

object App01ImplMergeHaltBoth extends App {

  println("\n-----")

  // type Pipe2[F[_], -I, -I2, +O] = (Stream[F, I], Stream[F, I2]) => Stream[F, O]

  /** Like `merge`, but halts as soon as _either_ branch halts. */
  def mergeHaltBoth[F[_] : Concurrent, O]: Pipe2[F, O, O, O] = { (s1, s2) =>
    /*
      How to implement ???
      ????????????????????????????????????????
    */
    ???
  }


  import cats.effect.ContextShift

  // This normally comes from IOApp
  implicit val ioContextShift: ContextShift[IO] = IO.contextShift(scala.concurrent.ExecutionContext.Implicits.global)
  // ioContextShift: cats.effect.ContextShift[cats.effect.IO] = cats.effect.internals.IOContextShift@eb64a2b

  val s1 = Stream(1, 2, 3).covary[IO]
  val s2 = Stream.eval(IO {Thread.sleep(200); 4} )
  val s3 = Stream(4).covary[IO]

  // The merge function runs two streams concurrently, combining their outputs. It halts when either input has halted:
  val merged = s1 mergeHaltBoth s2

  // ---- see also mergeHaltL and mergeHaltR

  val res = merged.compile.toVector.unsafeRunSync()
  // res: Vector[Int] = Vector(1, 2, 3)
  println(res)

  println("-----\n")
}
