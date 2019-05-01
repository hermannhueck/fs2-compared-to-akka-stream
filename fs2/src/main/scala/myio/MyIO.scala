package myio

import cats.effect.{ExitCase, Sync}

import scala.concurrent.duration.Duration
import scala.concurrent.{Await, ExecutionContext, Future}
import scala.language.higherKinds
import scala.util.Try

sealed trait MyIO[+A] extends Product with Serializable {

  import MyIO._

  protected def run(): A

  def unsafeRunSync(): A = run()

  def flatMap[B](f: A => MyIO[B]): MyIO[B] = FlatMap(this, f)

  def map[B](f: A => B): MyIO[B] = flatMap(a => pure(f(a)))

  def flatten[B](implicit ev: A <:< MyIO[B]): MyIO[B] = flatMap(a => a)

  // ----- impure sync run* methods

  // runs on the current Thread returning Try[A]
  def runToTry: Try[A] = Try {
    run()
  }

  // runs on the current Thread returning Either[Throwable, A]
  def runToEither: Either[Throwable, A] = runToTry.toEither

  // ----- impure async run* methods

  // returns a Future that runs the task eagerly on another thread
  def runToFuture(implicit ec: ExecutionContext): Future[A] = Future {
    run()
  }

  // runs the IO in a Runnable on the given ExecutionContext
  // and then executes the specified Try based callback
  def runOnComplete(callback: Try[A] => Unit)(implicit ec: ExecutionContext): Unit =
    runToFuture onComplete callback

  // runs the IO in a Runnable on the given ExecutionContext
  // and then executes the specified Either based callback
  def runAsync(callback: Either[Throwable, A] => Unit)(implicit ec: ExecutionContext): Unit =
    runOnComplete(tryy => callback(tryy.toEither))

  // Triggers async evaluation of this IO, executing the given function for the generated result.
  // WARNING: Will not be called if this IO is never completed or if it is completed with a failure.
  // Since this method executes asynchronously and does not produce a return value,
  // any non-fatal exceptions thrown will be reported to the ExecutionContext.
  def foreach(f: A => Unit)(implicit ec: ExecutionContext): Unit =
    runAsync {
      case Left(ex) => ec.reportFailure(ex)
      case Right(value) => f(value)
    }

  // Returns a failed projection of this task.
  //
  // The failed projection is a Task holding a value of type Throwable, emitting the error yielded by the source,
  // in case the source fails, otherwise if the source succeeds the result will fail with a NoSuchElementException.
  def failed: MyIO[Throwable] = Failed(this)

  def onErrorHandleWith[AA >: A](f: Throwable => MyIO[AA]): MyIO[AA] = MyIO {
    this.runToEither match {
      case Left(t) => f(t)
      case Right(a) => MyIO.pure(a)
    }
  }.flatten

  def onErrorHandle[AA >: A](f: Throwable => AA): MyIO[AA] =
    onErrorHandleWith(t => MyIO.pure(f(t)))

  def onErrorRecoverWith[AA >: A](pf: PartialFunction[Throwable, MyIO[AA]]): MyIO[AA] =
    onErrorHandleWith { t => pf.applyOrElse(t, raiseError) }

  def onErrorRecover[AA >: A](pf: PartialFunction[Throwable, AA]): MyIO[AA] =
    onErrorHandle { t => pf.applyOrElse(t, throw _: Throwable) }

  def onErrorRestartIf(p: Throwable => Boolean): MyIO[A] =
    onErrorHandleWith { t =>
      if (p(t))
        onErrorRestartIf(p)
      else
        MyIO.raiseError(t)
    }

  def onErrorRestart(maxRetries: Long): MyIO[A] =
    onErrorHandleWith { t =>
      if (maxRetries > 0)
        onErrorRestart(maxRetries - 1)
      else
        raiseError(t)
    }

  def onErrorFallbackTo[B >: A](that: MyIO[B]): MyIO[B] =
    onErrorHandleWith(_ => that)

  def attempt[AA >: A]: MyIO[Either[Throwable, AA]] =
    this
      .map { t => Right(t): Either[Throwable, A] }
      .onErrorHandleWith { e => MyIO.pure(Left(e))}

  // Turns a successful value into an error if it does not satisfy a given predicate. See cats.MonadError
  def ensure(error: => Throwable)(predicate: A => Boolean): MyIO[A] =
    ensureOr(_ => error)(predicate)

  // Turns a successful value into an error specified by the `error` function if it does not satisfy a given predicate. See cats.MonadError
  def ensureOr(error: A => Throwable)(predicate: A => Boolean): MyIO[A] = MyIO {
    this.runToEither match {
      case Left(throwable) => raiseError(throwable)
      case Right(value) if predicate(value) => pure(value)
      case Right(value) => raiseError(error(value))
    }
  }.flatten

  def bracketCase[B](use: A => MyIO[B])(release: (A, ExitCase[Throwable]) => MyIO[Unit]): MyIO[B] = MyIO {
    this flatMap { resource =>
      try {
        import cats.syntax.apply._
        use(resource) <* release(resource, ExitCase.complete)
      } catch {
        case t: Throwable =>
          release(resource, ExitCase.error(t))
          throw t
      }
    }
  }.flatten
}

object MyIO {

  private case class Pure[A](thunk: () => A) extends MyIO[A] {
    override def run(): A = thunk()
  }
  private case class Eval[A](thunk: () => A) extends MyIO[A] {
    override def run(): A = thunk()
  }
  private case class Error[A](exception: Throwable) extends MyIO[A] {
    override def run(): A = throw exception
  }
  private case class Failed[A](io: MyIO[A]) extends MyIO[Throwable] {
    override def run(): Throwable = try {
      io.run()
      throw new NoSuchElementException("failed")
    } catch {
      case nse: NoSuchElementException if nse.getMessage == "failed" => throw nse
      case throwable: Throwable => throwable
    }
  }
  private case class Suspend[A](thunk: () => MyIO[A]) extends MyIO[A] {
    override def run(): A = thunk().run()
  }
  private case class FlatMap[A, B](src: MyIO[A], f: A => MyIO[B]) extends MyIO[B] {
    override def run(): B = f(src.run()).run()
  }
  private case class FromFuture[A](fa: Future[A]) extends MyIO[A] {
    override def run(): A = Await.result(fa, Duration.Inf) // BLOCKING!!!
    // A solution of this problem would require a redesign of this simple IO Monod, which doesn't really support async computations.
  }

  def pure[A](a: A): MyIO[A] = Pure { () => a }
  def now[A](a: A): MyIO[A] = pure(a)

  def raiseError[A](t: Throwable): MyIO[A] = Error[A](t)

  def eval[A](a: => A): MyIO[A] = Eval { () => a }
  def delay[A](a: => A): MyIO[A] = eval(a)
  def apply[A](a: => A): MyIO[A] = eval(a)

  def suspend[A](ioa: => MyIO[A]): MyIO[A] = Suspend(() => ioa)
  def defer[A](ioa: => MyIO[A]): MyIO[A] = suspend(ioa)

  def fromTry[A](tryy: Try[A]): MyIO[A] =
    tryy.fold(MyIO.raiseError, MyIO.pure)

  def fromEither[A](either: Either[Throwable, A]): MyIO[A] =
    either.fold(MyIO.raiseError, MyIO.pure)

  def fromFuture[A](future: Future[A]): MyIO[A] = FromFuture(future)

  def deferFuture[A](future: => Future[A]): MyIO[A] =
    defer(MyIO.fromFuture(future))

  // Bracket instance defined in implicit scope
  implicit def ioMonad: Sync[MyIO] = new Sync[MyIO] {

    // Monad
    override def pure[A](value: A): MyIO[A] = MyIO.pure(value)
    override def flatMap[A, B](fa: MyIO[A])(f: A => MyIO[B]): MyIO[B] = fa flatMap f
    override def tailRecM[A, B](a: A)(f: A => MyIO[Either[A, B]]): MyIO[B] = ???

    // MonadError
    override def raiseError[A](e: Throwable): MyIO[A] = raiseError(e)
    override def handleErrorWith[A](fa: MyIO[A])(f: Throwable => MyIO[A]): MyIO[A] = fa onErrorHandleWith f

    // Bracket
    override def bracket[A, B](acquire: MyIO[A])(use: A => MyIO[B])(release: A => MyIO[Unit]): MyIO[B] =
      acquire.bracket(use)(release)
    override def bracketCase[A, B](acquire: MyIO[A])(use: A => MyIO[B])(release: (A, ExitCase[Throwable]) => MyIO[Unit]): MyIO[B] =
      acquire.bracketCase(use)(release)

    override def suspend[A](thunk: => MyIO[A]): MyIO[A] = MyIO.suspend(thunk)
  }

  implicit class syntax[A](ioa: MyIO[A]) { // provide corresponding methods of ApplicativeError/MonadError

    def handleErrorWith(f: Throwable => MyIO[A]): MyIO[A] = ioa onErrorHandleWith f
    def handleError(f: Throwable => A): MyIO[A] = ioa onErrorHandle f
    def recoverWith(pf: PartialFunction[Throwable, MyIO[A]]): MyIO[A] = ioa onErrorRecoverWith pf
    def recover(pf: PartialFunction[Throwable, A]): MyIO[A] = ioa onErrorRecover pf

    def bracket[B](use: A => MyIO[B])(release: A => MyIO[Unit]): MyIO[B] =
      ioa.bracketCase(use)((a, _) => release(a))
  }
}

