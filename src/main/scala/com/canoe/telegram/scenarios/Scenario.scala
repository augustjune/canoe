package com.canoe.telegram.scenarios

import cats.{Applicative, Monad, MonadError}
import cats.syntax.all._
import com.canoe.telegram.models.messages.TelegramMessage
import fs2.{Pipe, Pull, Stream}

object Scenario {

  implicit def monadInstance[F[_]](implicit F: Applicative[F]): Monad[Scenario[F, ?]] =
    new Monad[Scenario[F, ?]] {

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

  def tolerate[B](fn: TelegramMessage => F[B]): Scenario[F, A] = tolerateN(fn)(1)

  def tolerateN[B](fn: TelegramMessage => F[B])(n: Int): Scenario[F, A] = {

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

  //  override def recover[B](fn: TelegramMessage => F[B]): Scenario[F, TelegramMessage] = new Scenario[F, TelegramMessage] {
  //    def fulfill(messages: Stream[F, TelegramMessage]): Stream[F, (Result[TelegramMessage], Stream[F, TelegramMessage])] =
  //      messages.head.flatMap { m =>
  //        if (p(m)) self.fulfill(messages.cons1(m))
  //        else Stream.eval(fn(m)) *> self.fulfill(messages)
  //      }
  //  }

  //  def recoverWith(fn: TelegramMessage => Scenario[F, TelegramMessage]): Scenario[F, TelegramMessage] =
  //    new Scenario[F, TelegramMessage] {
  //      def fulfill(messages: Stream[F, TelegramMessage]): Stream[F, (Result[TelegramMessage], Stream[F, TelegramMessage])] =
  //        messages.head.flatMap { m =>
  //          if (p(m)) self.fulfill(messages.cons1(m))
  //          else fn(m).fulfill(messages)
  //        }
  //    }

  //  def or[B](other: => Scenario[F, B]): Scenario[F, Either[TelegramMessage, B]] =
  //    new Scenario[F, Either[TelegramMessage, B]] {
  //      def fulfill(messages: Stream[F, TelegramMessage]): Stream[F, (Result[Either[TelegramMessage, B]], Stream[F, TelegramMessage])] =
  //        messages.head.flatMap { m =>
  //          if (p(m)) self.fulfill(messages.cons1(m)).map { case (a, rest) => a.map(Left(_)) -> rest }
  //          else other.fulfill(messages.cons1(m)).map { case (a, rest) => a.map(Right(_)) -> rest }
  //        }
  //    }
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

  def limit(n: Int): Scenario[F, TelegramMessage] = new Scenario[F, TelegramMessage] {
    def fulfill(messages: Stream[F, TelegramMessage]): Stream[F, (Result[TelegramMessage], Stream[F, TelegramMessage])] = {

      def go(input: Stream[F, TelegramMessage]): Pull[F, (Result[TelegramMessage], Stream[F, TelegramMessage]), Unit] = {
        input.dropWhile(!p(_)).pull.uncons1.flatMap {
          case Some((m, rest)) =>
            Pull.output1(Right(m) -> rest) >> go(rest)

          case None => Pull.done
        }
      }

      go(messages).stream
      }.take(n)
  }
}


final case class Action[F[_], A](fa: F[A]) extends Scenario[F, A] {
  def fulfill(messages: Stream[F, TelegramMessage]): Stream[F, (Result[A], Stream[F, TelegramMessage])] =
    Stream.eval(fa).map(Right(_) -> messages)
}