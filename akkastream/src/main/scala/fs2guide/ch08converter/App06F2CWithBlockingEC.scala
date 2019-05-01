package fs2guide.ch08converter

import java.nio.file.{Path, Paths}

import akka.NotUsed
import akka.actor.ActorSystem
import akka.stream.scaladsl.{FileIO, Flow, Framing, Keep, Sink, Source}
import akka.stream.{ActorMaterializer, IOResult}
import akka.util.ByteString
import com.typesafe.config.ConfigFactory

import scala.concurrent.duration._
import scala.concurrent.{Await, ExecutionContext, Future}

object App06F2CWithBlockingEC extends App {

  val config = ConfigFactory.parseString("""
    blocking-dispatcher {
      executor = "thread-pool-executor"
      thread-pool-executor {
        core-pool-size-min    = 16
        core-pool-size-max    = 16
      }
    }
    """)

  val temp = getClass.getSimpleName
  val appName = temp.substring(0, temp.length - 1)

  implicit val system: ActorSystem = ActorSystem(appName, config)
  implicit val materializer: ActorMaterializer = ActorMaterializer()
  implicit val ec: ExecutionContext = system.dispatcher
  val blockingExecutionContext: ExecutionContext = system.dispatchers.lookup("blocking-dispatcher")

  println(s"\n===== $appName =====")


  def fahrenheitToCelsius(f: Double): Double =
    (f - 32.0) * (5.0 / 9.0)

  private val input: Path = Paths.get("testdata/fahrenheit.txt")
  private val output = Paths.get("output/celsius-akka.txt")

  private val fileSource: Source[ByteString, Future[IOResult]] =
    FileIO.fromPath(input)
      .map { bs => printCurrentThread(); bs }
      .async("blocking-dispatcher")

  private val fileSink: Sink[ByteString, Future[IOResult]] =
    FileIO.toPath(output)
      //.mapMaterializedValue { future => printCurrentThread(); future }
      .async("blocking-dispatcher")

  val frameToLines: Flow[ByteString, String, NotUsed] =
    Framing.delimiter(ByteString("\n"), maximumFrameLength = 256, allowTruncation = true)
      .map(_.utf8String)

  val byteStringSource =
    fileSource
      .via(frameToLines)
      .filter(s => !s.trim.isEmpty && !s.startsWith("//"))
      .map(line => fahrenheitToCelsius(line.toDouble).toString)
      .intersperse("\n")
      .map(str => ByteString(str))

  val (inResult, outResult) =
    byteStringSource
      .toMat(fileSink)(Keep.both)
      .run()

  inResult foreach println
  Thread sleep 100L
  println("-----")
  outResult foreach println
  Thread sleep 100L


  Await.ready(system.terminate(), 3.seconds)
  println("=====\n")
}
