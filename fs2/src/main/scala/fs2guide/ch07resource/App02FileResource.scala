package fs2guide.ch07resource

import java.io.{BufferedInputStream, FileInputStream, InputStream}
import java.util.concurrent.Executors

import cats.effect.{ContextShift, IO}
import fs2.{Stream, text}

import scala.concurrent.{ExecutionContext, ExecutionContextExecutorService}

object App02FileResource extends App {

  println("\n-----")


  implicit val blockingEC: ExecutionContextExecutorService = ExecutionContext.fromExecutorService(Executors.newFixedThreadPool(2))
  implicit val cs: ContextShift[IO] = IO.contextShift(blockingEC)


  val acquire: IO[InputStream] = IO {
    println("========>>>>> opening file ...")
    new BufferedInputStream(new FileInputStream("README.md"))
  }

  val release: InputStream => IO[Unit] = in => IO {
    println("========>>>>> closing file ...")
    in.close()
  }

  val readerStream: Stream[IO, InputStream] = Stream.bracket(acquire)(release)
  val byteStream: Stream[IO, Byte] = readerStream.flatMap {
    in: InputStream => fs2.io.readInputStream(IO(in), 64, blockingEC)
  }
  val linesStream: Stream[IO, String] =
    byteStream
      .through(text.utf8Decode)
      .through(text.lines) // ++ Stream.eval[IO, Byte](IO(throw new RuntimeException("byte stream error")))

  println("\n>>>>>>>>>>>>>> print lines:")
  val ioLines: IO[Vector[String]] = linesStream.compile.toVector
  ioLines.unsafeRunSync() foreach println

  println("\n>>>>>>>>>>>>>> count lines:")
  val ioCount: IO[Int] = linesStream.compile.fold(0)((sum, _) => sum+1)
  println(ioCount.unsafeRunSync())

  println("\n>>>>>>>>>>>>>> print lines again (assuming the the file contents doesn't fit into memory):")
//  val ioUnit: IO[Unit] = linesStream.map { line => println(line); line }.compile.drain
  val ioUnit: IO[Unit] = linesStream.lines(java.lang.System.out).compile.drain
  ioUnit.unsafeRunSync()


  // The inner stream fails, but notice the release action is still run:

  blockingEC.shutdown()
  println("-----\n")
}
