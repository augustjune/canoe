package com.canoe.telegram.scenarios

import cats.{Applicative, MonadError}
import com.canoe.telegram.models.messages.TelegramMessage
import fs2.{Pipe, Pull, Stream}

object Scenario {

  def receive[F[_]](p: PartialFunction[TelegramMessage, Boolean]): Scenario[F, TelegramMessage] =
    Receive(p.applyOrElse(_, (_: Any) => false))

  def expect[F[_]](p: PartialFunction[TelegramMessage, Boolean]): Scenario[F, TelegramMessage] =
    Expect(p.applyOrElse(_, (_: Any) => false))

  def eval[F[_], A](fa: F[A]): Scenario[F, A] =
    Action(fa)

  def unit[F[_]](implicit F: Applicative[F]) =
    Action(F.unit)

  def drain[F[_], A]: Scenario[F, A] =
    new Scenario[F, A] {
      def fulfill(messages: Stream[F, TelegramMessage]): Stream[F, (Result[A], Stream[F, TelegramMessage])] =
        Stream.empty
    }

  implicit def monadErrorInstance[F[_]](implicit F: Applicative[F]): MonadError[Scenario[F, ?], TelegramMessage] =
    new MonadError[Scenario[F, ?], TelegramMessage] {
      def raiseError[A](e: TelegramMessage): Scenario[F, A] =
        new Scenario[F, A] {
          def fulfill(messages: Stream[F, TelegramMessage]): Stream[F, (Result[A], Stream[F, TelegramMessage])] =
            Stream(Missed(e) -> messages)
        }

      def handleErrorWith[A](fa: Scenario[F, A])(f: TelegramMessage => Scenario[F, A]): Scenario[F, A] = {
        new Scenario[F, A] {
          def fulfill(messages: Stream[F, TelegramMessage]): Stream[F, (Result[A], Stream[F, TelegramMessage])] =
            fa.fulfill(messages).flatMap {
              case (Missed(m), rest) =>
                f(m).fulfill(rest)

              case success => Stream(success)
            }
        }
      }

      override def pure[A](x: A): Scenario[F, A] =
        Action(F.pure(x))

      override def flatMap[A, B](fa: Scenario[F, A])(f: A => Scenario[F, B]): Scenario[F, B] =
        fa.flatMap(f)

      override def tailRecM[A, B](a: A)(f: A => Scenario[F, Either[A, B]]): Scenario[F, B] =
        f(a).flatMap {
          case Left(a) => tailRecM(a)(f)
          case Right(b) => Action(F.pure(b))
        }
    }

  sealed trait Result[+A] {
    def map[B](f: A => B): Result[B] = this match {
      case Matched(a) => Matched(f(a))
      case same @ Missed(_) => same
      case same @ Cancelled(_) => same
    }
  }

  case class Matched[A](a: A) extends Result[A]
  case class Missed(message: TelegramMessage) extends Result[Nothing]
  case class Cancelled(message: TelegramMessage) extends Result[Nothing]

}

import Scenario._

sealed abstract class Scenario[F[_], A] extends Pipe[F, TelegramMessage, A] {
  self =>

  def apply(messages: Stream[F, TelegramMessage]): Stream[F, A] =
    fulfill(messages).map(_._1).collect { case Matched(a) => a }

  def fulfill(messages: Stream[F, TelegramMessage]): Stream[F, (Result[A], Stream[F, TelegramMessage])]

  def map[B](fn: A => B): Scenario[F, B] = new Scenario[F, B] {
    def fulfill(messages: Stream[F, TelegramMessage]): Stream[F, (Result[B], Stream[F, TelegramMessage])] =
      self.fulfill(messages).map {
        case (a, rest) => (a.map(fn), rest)
      }
  }

  def flatMap[B](fn: A => Scenario[F, B]): Scenario[F, B] = {
    new Scenario[F, B] {
      def fulfill(messages: Stream[F, TelegramMessage]): Stream[F, (Result[B], Stream[F, TelegramMessage])] =
        self.fulfill(messages).flatMap {
          case (Matched(a), rest) => fn(a).fulfill(rest)
          case (Missed(m), rest) => Stream(Missed(m) -> rest)
          case (Cancelled(m), rest) => Stream(Cancelled(m) -> rest)
        }

    }
  }

  def tolerate[B](fn: TelegramMessage => F[B]): Scenario[F, A] = tolerateN(1)(fn)

  def tolerateN[B](n: Int)(fn: TelegramMessage => F[B]): Scenario[F, A] = {

    def tolerateFulfill(n: Int, messages: Stream[F, TelegramMessage]): Stream[F, (Result[A], Stream[F, TelegramMessage])] =
      if (n <= 0) self.fulfill(messages)
      else self.fulfill(messages).flatMap {
        case (Cancelled(_), rest) => self.fulfill(rest)
        case (Missed(m), rest) => Stream.eval(fn(m)) >> tolerateFulfill(n - 1, rest)
        case matched => Stream(matched)
      }

    new Scenario[F, A] {
      def fulfill(messages: Stream[F, TelegramMessage]): Stream[F, (Result[A], Stream[F, TelegramMessage])] =
        tolerateFulfill(n, messages)
    }
  }

  // Doesn't keep the history of matched messages
  // (second scenario starts matching from the point where the first one missed / was cancelled)
  def or[B](other: => Scenario[F, B]): Scenario[F, Either[A, B]] =
    new Scenario[F, Either[A, B]] {
      def fulfill(messages: Stream[F, TelegramMessage]): Stream[F, (Result[Either[A, B]], Stream[F, TelegramMessage])] =
        self.fulfill(messages).flatMap {
          case (Matched(a), rest) =>
            Stream(Matched(Left(a)) -> rest)

          case (Missed(m), rest) =>
            other.fulfill(rest.cons1(m)).map {
              case (Matched(b), rest2) => Matched(Right(b)) -> rest2
              case (Missed(m), rest2) => Missed(m) -> rest2
              case (Cancelled(m), rest2) => Cancelled(m) -> rest2
            }

          // Try second scenario even if the first one was cancelled (should the cancellation message be read? [other.fulfill(rest.cons1(m)).map])
          case (Cancelled(m), rest) =>
            other.fulfill(rest.cons1(m)).map {
              case (Matched(b), rest2) => Matched(Right(b)) -> rest2
              case (Missed(m), rest2) => Missed(m) -> rest2
              case (Cancelled(m), rest2) => Cancelled(m) -> rest2
            }
        }
    }

  def drain: Scenario[F, Unit] = flatMap(_ => Scenario.drain)
}

object Expect {

  def any[F[_]]: Expect[F] = Expect(_ => true)

  def collect[F[_]]: ExpectCollectPartiallyApplied[F] = new ExpectCollectPartiallyApplied[F]

  final class ExpectCollectPartiallyApplied[F[_]](val dummy: Boolean = true) extends AnyVal {
    def apply[A](p: PartialFunction[TelegramMessage, A]): Scenario[F, A] =
      Expect(p.isDefinedAt).map(p)
  }

}

final case class Expect[F[_]](p: TelegramMessage => Boolean) extends Scenario[F, TelegramMessage] {

  def fulfill(messages: Stream[F, TelegramMessage]): Stream[F, (Result[TelegramMessage], Stream[F, TelegramMessage])] = {

    def go(input: Stream[F, TelegramMessage]): Pull[F, (Result[TelegramMessage], Stream[F, TelegramMessage]), Unit] =
      input.pull.uncons1.flatMap {
        case Some((m, rest)) =>
          if (p(m)) Pull.output1(Matched(m) -> rest)
          else Pull.output1(Missed(m) -> rest)

        case None => Pull.done
      }

    go(messages).stream
  }
}

object Receive {

  def any[F[_]]: Receive[F] = Receive(_ => true)

  def collect[F[_]]: ReceiveCollectPartiallyApplied[F] = new ReceiveCollectPartiallyApplied[F]

  final class ReceiveCollectPartiallyApplied[F[_]](val dummy: Boolean = true) extends AnyVal {
    def apply[A](p: PartialFunction[TelegramMessage, A]): Scenario[F, A] =
      Receive(p.isDefinedAt).map(p)
  }

}

final case class Receive[F[_]](p: TelegramMessage => Boolean) extends Scenario[F, TelegramMessage] {

  def fulfill(messages: Stream[F, TelegramMessage]): Stream[F, (Result[TelegramMessage], Stream[F, TelegramMessage])] = {

    def go(input: Stream[F, TelegramMessage]): Pull[F, (Result[TelegramMessage], Stream[F, TelegramMessage]), Unit] = {
      input.dropWhile(!p(_)).pull.uncons1.flatMap {
        case Some((m, rest)) =>
          Pull.output1(Matched(m) -> rest) >> go(rest)

        case None => Pull.done
      }
    }

    go(messages).stream
  }
}

final case class Action[F[_], A](fa: F[A]) extends Scenario[F, A] {
  def fulfill(messages: Stream[F, TelegramMessage]): Stream[F, (Result[A], Stream[F, TelegramMessage])] =
    Stream.eval(fa).map(Matched(_) -> messages)
}
