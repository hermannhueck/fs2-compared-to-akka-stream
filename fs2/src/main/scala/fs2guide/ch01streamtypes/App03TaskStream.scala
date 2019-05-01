package fs2guide.ch01streamtypes

import fs2.Stream

//import monix.eval.Task
//import monix.execution.Scheduler.Implicits.global

import scala.concurrent.duration._

object App03TaskStream extends App {

  println("\n----- Stream of Task effects")

/*
  val stream: Stream[Task, Int] = Stream.eval(Task {
    println("BEING RUN!!"); 1 + 1
  }).repeat.take(3)

  val effect: Task[Vector[Int]] = stream.compile.toVector   // Up to this point nothing is run

  val result: Vector[Int] = effect.runSyncUnsafe(3.seconds)    // produces side effects (println) and returns the result

  println(result)
*/

  println("-----\n")
}
