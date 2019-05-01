package fs2guide.ch22concurrencyprimitives

import cats.effect._
import cats.syntax.all._
import fs2.concurrent.{SignallingRef, Topic}
import fs2.{Pipe, Stream}

import scala.concurrent.duration._

import scala.language.higherKinds


sealed trait Event
case class Text(value: String) extends Event
case object Quit extends Event

class EventService[F[_]](eventsTopic: Topic[F, Event], interrupter: SignallingRef[F, Boolean])(
  implicit F: Concurrent[F], timer: Timer[F]) {

  // Publishing 15 text events, then single Quit event, and still publishing text events
  def startPublisher: Stream[F, Unit] = {

    val currentTime: Stream[F, Text] =
      Stream
        .eval(F.delay(System.currentTimeMillis).map(time => Text(time.toString)))
        .repeat

    val textEvents: Stream[F, Unit] =
      Stream
        .awakeEvery[F](1.second)
        .zipRight(currentTime)
        .through(eventsTopic.publish)

    val quitEvent: Stream[F, Unit] = Stream.eval(eventsTopic.publish1(Quit))

    (textEvents.take(15) ++ quitEvent ++ textEvents).interruptWhen(interrupter)
  }

  // Creating 3 subscribers in a different period of time and join them to run concurrently
  def startSubscribers: Stream[F, Unit] = {

    def processEvent(subscriberNumber: Int): Pipe[F, Event, Unit] = eventStream =>
      eventStream.flatMap {
        case e @ Text(_) =>
          Stream.eval(F.delay(println(s"Subscriber #$subscriberNumber processing event: $e")))
        case Quit =>
          Stream.eval(interrupter.set(true))
      }

    val events: Stream[F, Event] = eventsTopic.subscribe(10)

    Stream(
      events.delayBy(0.seconds).through(processEvent(1)),
      events.delayBy(5.seconds).through(processEvent(2)),
      events.delayBy(10.seconds).through(processEvent(3))
    ).parJoin(3)
  }
}

object App02PubSub extends IOApp {

  val program: Stream[IO, Unit] = for {
    topic <- Stream.eval(Topic[IO, Event](Text("Initial Event")))
    signal <- Stream.eval(SignallingRef[IO, Boolean](false))
    service = new EventService[IO](topic, signal)
    _ <- service.startPublisher concurrently service.startSubscribers
  } yield ()

  override def run(args: List[String]): IO[ExitCode] =
    program.compile.drain.as(ExitCode.Success)
}
