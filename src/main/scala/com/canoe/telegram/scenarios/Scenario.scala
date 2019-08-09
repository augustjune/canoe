package com.canoe.telegram.scenarios

import cats.syntax.all._
import com.canoe.telegram.models.messages.TelegramMessage
import fs2.{Pipe, Pull, Stream}

sealed abstract class Scenario[F[_], A] extends Pipe[F, TelegramMessage, A] {
  self =>

  def apply(messages: Stream[F, TelegramMessage]): Stream[F, A] =
    fulfill(messages).map(_._1)

  def fulfill(messages: Stream[F, TelegramMessage]): Stream[F, (A, Stream[F, TelegramMessage])]

  def map[B](fn: A => B): Scenario[F, B] = new Scenario[F, B] {
    def fulfill(messages: Stream[F, TelegramMessage]): Stream[F, (B, Stream[F, TelegramMessage])] =
      self.fulfill(messages).map { case (a, rest) => (fn(a), rest) }
  }

  def flatMap[B](fn: A => Scenario[F, B]): Scenario[F, B] = {
    new Scenario[F, B] {
      def fulfill(messages: Stream[F, TelegramMessage]): Stream[F, (B, Stream[F, TelegramMessage])] =
        self.fulfill(messages).flatMap { case (a, rest) => fn(a).fulfill(rest) }
    }
  }
}


final case class Receive[F[_]](p: TelegramMessage => Boolean) extends Scenario[F, TelegramMessage] {
  self =>

  def fulfill(messages: Stream[F, TelegramMessage]): Stream[F, (TelegramMessage, Stream[F, TelegramMessage])] = {

    def go(input: Stream[F, TelegramMessage]): Pull[F, (TelegramMessage, Stream[F, TelegramMessage]), Unit] = {
      input.dropWhile(!p(_)).pull.uncons1.flatMap {
        case Some((m, rest)) =>
          Pull.output1(m -> rest) >> go(rest)

        case None => Pull.done
      }
    }

    go(messages).stream
  }

  def limit(n: Int): Scenario[F, TelegramMessage] = new Scenario[F, TelegramMessage] {
    def fulfill(messages: Stream[F, TelegramMessage]): Stream[F, (TelegramMessage, Stream[F, TelegramMessage])] = {

      def go(input: Stream[F, TelegramMessage]): Pull[F, (TelegramMessage, Stream[F, TelegramMessage]), Unit] = {
        input.dropWhile(!p(_)).pull.uncons1.flatMap {
          case Some((m, rest)) =>
            Pull.output1(m -> rest) >> go(rest)

          case None => Pull.done
        }
      }

      go(messages).stream
    }.take(n)
  }
}

final case class Expect[F[_]](p: TelegramMessage => Boolean) extends Scenario[F, TelegramMessage] {
  self =>

  def fulfill(messages: Stream[F, TelegramMessage]): Stream[F, (TelegramMessage, Stream[F, TelegramMessage])] =
    messages.head
      .filter(p)
      .map(_ -> messages)

  def recover[B](fn: TelegramMessage => F[B]): Scenario[F, TelegramMessage] = new Scenario[F, TelegramMessage] {
    def fulfill(messages: Stream[F, TelegramMessage]): Stream[F, (TelegramMessage, Stream[F, TelegramMessage])] =
      messages.head.flatMap { m =>
        if (p(m)) self.fulfill(messages.cons1(m))
        else Stream.eval(fn(m)) *> self.fulfill(messages)
      }
  }

  def recoverWith(fn: TelegramMessage => Scenario[F, TelegramMessage]): Scenario[F, TelegramMessage] =
    new Scenario[F, TelegramMessage] {
      def fulfill(messages: Stream[F, TelegramMessage]): Stream[F, (TelegramMessage, Stream[F, TelegramMessage])] =
        messages.head.flatMap { m =>
          if (p(m)) self.fulfill(messages.cons1(m))
          else fn(m).fulfill(messages)
        }
    }

  def or[B](other: => Scenario[F, B]): Scenario[F, Either[TelegramMessage, B]] =
    new Scenario[F, Either[TelegramMessage, B]] {
      def fulfill(messages: Stream[F, TelegramMessage]): Stream[F, (Either[TelegramMessage, B], Stream[F, TelegramMessage])] =
        messages.head.flatMap { m =>
          if (p(m)) self.fulfill(messages.cons1(m)).map { case (a, rest) => Left(a) -> rest }
          else other.fulfill(messages.cons1(m)).map { case (a, rest) => Right(a) -> rest }
        }
    }
}

final case class Action[F[_], A](fa: F[A]) extends Scenario[F, A] {
  def fulfill(messages: Stream[F, TelegramMessage]): Stream[F, (A, Stream[F, TelegramMessage])] =
    Stream.eval(fa).map(_ -> messages)
}