package asdoc.tweets

import akka.stream.scaladsl._
import akka.stream.{ClosedShape, Graph}
import akka.{Done, NotUsed}
import akkautil.AkkaStreamApp

import scala.concurrent.duration._
import scala.concurrent.{Await, Future}


object App06TweetsBroadcast extends AkkaStreamApp {

  import Tweets._

  val tweetSource: Source[Tweet, NotUsed] = Source(tweets)

  val hashtags: Source[Hashtag, NotUsed] =
    tweetSource
      .mapConcat(_.hashtags.toList)

  val sinkAuthors: Sink[Author, Future[Done]] = Sink foreach[Author] println
  val sinkHashtags: Sink[Hashtag, Future[Done]] = Sink foreach[Hashtag] println

  val graph: Graph[ClosedShape, NotUsed] = GraphDSL.create() { implicit builder =>

    import GraphDSL.Implicits._

    val broadcast = builder.add(Broadcast[Tweet](2))
    tweetSource ~> broadcast.in
    broadcast.out(0) ~> Flow[Tweet].map(_.author) ~> sinkAuthors
    broadcast.out(1) ~> Flow[Tweet].mapConcat(_.hashtags.toList) ~> sinkHashtags
    ClosedShape
  }

  val rg: RunnableGraph[NotUsed] = RunnableGraph.fromGraph(graph)
  rg.run()


  Await.result(system.terminate(), 3.seconds)
  println("=====\n")
}
