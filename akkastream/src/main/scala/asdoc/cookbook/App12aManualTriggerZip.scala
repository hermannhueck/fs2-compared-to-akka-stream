package asdoc.cookbook

import akka.actor.Cancellable
import akka.{Done, NotUsed}
import akka.stream.{ClosedShape, ThrottleMode}
import akka.stream.scaladsl.{Flow, GraphDSL, RunnableGraph, Sink, Source, Zip}
import akkautil.AkkaStreamApp

import scala.concurrent.{Await, Future}
import scala.concurrent.duration._

object App12aManualTriggerZip extends AkkaStreamApp {

  type Trigger = Unit
  type Message = String

  val elements: Source[Message, NotUsed] = Source(List("1", "2", "3", "4", "5", "6"))
  val triggerSource: Source[Trigger, Cancellable] = Source.tick(2.seconds, 500.milliseconds, ())
  val sink: Sink[Message, Future[Done]] = Sink.foreach[Message](println)

  val graph: RunnableGraph[NotUsed] = RunnableGraph.fromGraph(GraphDSL.create() { implicit builder =>

    import GraphDSL.Implicits._

    val zip = builder.add(Zip[Message, Trigger]())

    elements ~> zip.in0
    triggerSource ~> zip.in1
    zip.out ~> Flow[(Message, Trigger)].map { case (msg, trigger) => msg } ~> sink

    ClosedShape
  })

  graph.run()
  Thread sleep 6000L


  Await.ready(system.terminate(), 3.seconds)
  println("-----\n")
}
