package fs2guide.ch07resource

import java.io.{BufferedInputStream, FileInputStream, InputStream}
import java.util.concurrent.Executors

import cats.effect.{ContextShift, IO}
import fs2.{Stream, text}

import scala.concurrent.{ExecutionContext, ExecutionContextExecutorService}

object App03ExecutionContextResource extends App {

  println("\n-----")


  val allocateEC: IO[ExecutionContextExecutorService] = IO {
    println("========>>>>> allocating ExecutionContext ...")
    ExecutionContext.fromExecutorService(Executors.newFixedThreadPool(2))
  }

  val shutdownEC: ExecutionContextExecutorService => IO[Unit] = ec => IO {
    println("========>>>>> shutting down ExecutionContext ...")
    ec.shutdown()
  }

  val ecStream: Stream[IO, ExecutionContextExecutorService] = Stream.bracket(allocateEC)(shutdownEC)


  val openFile: IO[InputStream] = IO {
    println("=====>>>>> opening file ...")
    new BufferedInputStream(new FileInputStream("README.md"))
  }

  val closeFile: InputStream => IO[Unit] = in => IO {
    println("=====>>>>> closing file ...")
    in.close()
  }

  val readerStream: Stream[IO, InputStream] = Stream.bracket(openFile)(closeFile)


  val linesStream = ecStream flatMap { blockingEC =>

    implicit val cs: ContextShift[IO] = IO.contextShift(ExecutionContext.global)

    readerStream.flatMap { in: InputStream =>
      fs2.io
        .readInputStream(IO(in), 64, blockingEC)
        .through(text.utf8Decode)
        .through(text.lines) // ++ Stream.eval[IO, Byte](IO(throw new RuntimeException("byte stream error")))
    }
  }

  println("\n>>>>>>>>>>>>>> count lines:")
  val ioCount: IO[Int] = linesStream.compile.fold(0)((sum, _) => sum+1)
  println(ioCount.unsafeRunSync())


  println("-----\n")
}
