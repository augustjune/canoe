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
            Stream(Left(e) -> messages)
        }

      def handleErrorWith[A](fa: Scenario[F, A])(f: TelegramMessage => Scenario[F, A]): Scenario[F, A] = {
        new Scenario[F, A] {
          def fulfill(messages: Stream[F, TelegramMessage]): Stream[F, (Result[A], Stream[F, TelegramMessage])] =
            fa.fulfill(messages).flatMap {
              case (Left(m), rest) =>
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

  type Result[A] = Either[TelegramMessage, A]
}

import Scenario.Result

sealed abstract class Scenario[F[_], A] extends Pipe[F, TelegramMessage, A] {
  self =>

  def apply(messages: Stream[F, TelegramMessage]): Stream[F, A] =
    fulfill(messages).map(_._1).collect { case Right(a) => a }

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
          case (Right(a), rest) => fn(a).fulfill(rest)
          case (Left(m), rest) => Stream(Left(m) -> rest)
        }

    }
  }

  def tolerate[B](fn: TelegramMessage => F[B]): Scenario[F, A] = tolerateN(1)(fn)

  def tolerateN[B](n: Int)(fn: TelegramMessage => F[B]): Scenario[F, A] = {

    def tolerateFulfill(n: Int, messages: Stream[F, TelegramMessage]): Stream[F, (Result[A], Stream[F, TelegramMessage])] =
      if (n <= 0) self.fulfill(messages)
      else self.fulfill(messages).flatMap {
        case (Left(m), rest) => Stream.eval(fn(m)) >> tolerateFulfill(n - 1, rest)
        case matched => Stream(matched)
      }

    new Scenario[F, A] {
      def fulfill(messages: Stream[F, TelegramMessage]): Stream[F, (Result[A], Stream[F, TelegramMessage])] =
        tolerateFulfill(n, messages)
    }
  }

  def or[B](other: => Scenario[F, B]): Scenario[F, Either[A, B]] =
    new Scenario[F, Either[A, B]] {
      def fulfill(messages: Stream[F, TelegramMessage]): Stream[F, (Result[Either[A, B]], Stream[F, TelegramMessage])] =
        self.fulfill(messages).flatMap {
          case (Right(a), rest) =>
            Stream(Right(Left(a)) -> rest)

          case (Left(m), rest) =>
            other.fulfill(rest.cons1(m)).map {
              case (Right(b), rest2) => Right(Right(b)) -> rest2
              case (Left(m), rest2) => Left(m) -> rest2
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
          if (p(m)) Pull.output1(Right(m) -> rest)
          else Pull.output1(Left(m) -> rest)

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
          Pull.output1(Right(m) -> rest) >> go(rest)

        case None => Pull.done
      }
    }

    go(messages).stream
  }
}

final case class Action[F[_], A](fa: F[A]) extends Scenario[F, A] {
  def fulfill(messages: Stream[F, TelegramMessage]): Stream[F, (Result[A], Stream[F, TelegramMessage])] =
    Stream.eval(fa).map(Right(_) -> messages)
}
