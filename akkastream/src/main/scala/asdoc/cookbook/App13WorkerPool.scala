package asdoc.cookbook

import akka.NotUsed
import akka.stream.FlowShape
import akka.stream.scaladsl.{Balance, Flow, GraphDSL, Merge, Sink, Source}
import akkautil.AkkaStreamApp

import scala.concurrent.Await
import scala.concurrent.duration._

object App13WorkerPool extends AkkaStreamApp {

  val myJobs = Source(1 to 20)
  type Result = String

  def worker[IN]: Flow[IN, String, NotUsed] = Flow[IN].map(_.toString + " done")

  //#worker-pool
  def balancer[In, Out](worker: Flow[In, Out, Any], workerCount: Int): Flow[In, Out, NotUsed] = {
    import GraphDSL.Implicits._

    Flow.fromGraph(GraphDSL.create() { implicit builder =>
      val balancer = builder.add(Balance[In](workerCount, waitForAllDownstreams = true))
      val merge = builder.add(Merge[Out](workerCount))

      for (_ <- 1 to workerCount) {
        // for each worker, add an edge from the balancer to the worker, then wire
        // it to the merge element
        balancer ~> worker.async ~> merge
      }

      FlowShape(balancer.in, merge.out)
    })
  }

  val processedJobs: Source[Result, NotUsed] = myJobs.via(balancer(worker, 3))
  //#worker-pool

  val result: Seq[Result] = Await.result(processedJobs.limit(20).runWith(Sink.seq), 3.seconds)
  result foreach println
  assert(result.toSet == (1 to 20).toList.map(_.toString + " done").toSet)


  Await.ready(system.terminate(), 3.seconds)
  println("-----\n")
}
