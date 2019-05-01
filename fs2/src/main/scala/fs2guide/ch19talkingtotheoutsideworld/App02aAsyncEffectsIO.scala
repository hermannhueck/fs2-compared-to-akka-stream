package fs2guide.ch19talkingtotheoutsideworld

import cats.effect.IO
import fs2.Stream

import scala.language.higherKinds

object App02aAsyncEffectsIO extends App {

  println("\n-----")

  trait Connection {

    def readBytes(onSuccess: Array[Byte] => Unit, onFailure: Throwable => Unit): Unit

    def readBytesE(onComplete: Either[Throwable, Array[Byte]] => Unit): Unit =
      readBytes(bs => onComplete(Right(bs)), e => onComplete(Left(e)))

    override def toString = "<connection>"
  }

  val connection = new Connection {
    def readBytes(onSuccess: Array[Byte] => Unit, onFailure: Throwable => Unit): Unit = {
      Thread sleep 200L
      onSuccess(Array(0, 1, 2))
    }
  }

  val ioBytes: IO[Array[Byte]] = IO.async[Array[Byte]] { (callback: Either[Throwable, Array[Byte]] => Unit) =>
    connection.readBytesE(callback)
  }
  // ioBytes: cats.effect.IO[Array[Byte]] = IO$425428304

  println("\n>>> Evaluate IO directly ...")
  val ioRes = ioBytes.unsafeRunSync().toList
  // ioRes: List[Byte] = List(0, 1, 2)
  println(ioRes)

  println("\n>>> Evaluate IO in a Stream ...")
  val streamRes = Stream.eval(ioBytes).map(_.toList).compile.toVector.unsafeRunSync()
  // res: Vector[List[Byte]] = Vector(List(0, 1, 2))
  println(streamRes)

  println("-----\n")
}
