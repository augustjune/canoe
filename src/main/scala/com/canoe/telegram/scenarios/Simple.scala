package com.canoe.telegram.scenarios

import cats.{Applicative, Id}
import cats.effect.{ExitCode, IO, IOApp, Sync}
import cats.syntax.all._
import fs2.Stream

import scala.io.StdIn

object Simple extends IOApp {

  type Message = String

  def chat(m: Message): String = m.take(3) + m.length

  def log[F[_] : Sync, A](id: String)(s: A): F[A] = Sync[F].delay {
    println(s"$id: $s")
    s
  }

  implicit class MessageScenario[F[_], A <: Message](val scenario: Scenario[F, A]) {
    def withinChat: Scenario[F, A] = new Scenario[F, A] {
      def fulfill(messages: Stream[F, Message]): Stream[F, (A, Stream[F, Message])] =
        scenario.fulfill(messages).map { case (a, rest) => a -> rest.filter(chat(_) == chat(a)) }
    }
  }

  object Scenario {
    private def fullPredicate[A](pf: PartialFunction[A, Boolean]): A => Boolean =
      pf.applyOrElse(_, (_: A) => false)

    // ToDo - make builder methods return common abstraction i.e. Scenario
    //  (currently undesirable because of Expect methods, such as recover)
    def receive[F[_]](pf: PartialFunction[Message, Boolean]): Receive[F] =
      Receive(fullPredicate(pf))

    def receive[F[_]](p: Message => Boolean): Receive[F] =
      Receive(p)

    def expect[F[_]](pf: PartialFunction[Message, Boolean]): Expect[F] =
      Expect(fullPredicate(pf))

    def expect[F[_]](p: Message => Boolean): Expect[F] =
      Expect(p)

    def execute[F[_], A](fa: F[A]): Action[F, A] =
      Action(fa)

    def pure[F[_] : Applicative, A](a: A): Scenario[F, A] =
      execute(a.pure[F])

    def pure[A](a: A): Scenario[Id, A] =
      execute[Id, A](a)

    def unit[F[_] : Applicative]: Scenario[F, Unit] =
      pure(())

    def end[F[_], A]: Scenario[F, A] =
      new Scenario[F, A] {
        def fulfill(messages: Stream[F, Message]): Stream[F, (A, Stream[F, Message])] = Stream.empty
      }
  }

  sealed abstract class Scenario[F[_], A] extends (Stream[F, Message] => Stream[F, A]) {
    self =>

    def apply(messages: Stream[F, Message]): Stream[F, A] =
      fulfill(messages).map(_._1)

    def fulfill(messages: Stream[F, Message]): Stream[F, (A, Stream[F, Message])]

    def map[B](fn: A => B): Scenario[F, B] = new Scenario[F, B] {
      def fulfill(messages: Stream[F, Message]): Stream[F, (B, Stream[F, Message])] =
        self.fulfill(messages).map { case (a, rest) => (fn(a), rest) }
    }

    def flatMap[B](fn: A => Scenario[F, B]): Scenario[F, B] = {
      new Scenario[F, B] {
        def fulfill(messages: Stream[F, Message]): Stream[F, (B, Stream[F, Message])] =
          self.fulfill(messages).flatMap { case (a, rest) => fn(a).fulfill(rest) }
      }
    }
  }

  final case class Receive[F[_]](p: Message => Boolean) extends Scenario[F, Message] {
    def fulfill(messages: Stream[F, Message]): Stream[F, (Message, Stream[F, Message])] =
      messages.filter(p).map(_ -> messages)
  }

  final case class Expect[F[_]](p: Message => Boolean) extends Scenario[F, Message] {
    self =>

    def fulfill(messages: Stream[F, Message]): Stream[F, (Message, Stream[F, Message])] =
      messages.head.filter(p).map(_ -> messages)

    def recover[B](fn: Message => F[B]): Scenario[F, Message] = new Scenario[F, Message] {
      def fulfill(messages: Stream[F, Message]): Stream[F, (Message, Stream[F, Message])] =
        messages.head.flatMap { m =>
          if (p(m)) self.fulfill(Stream(m) ++ messages)
          else Stream.eval(fn(m)) *> self.fulfill(messages)
        }
    }

    def recoverWith(fn: Message => Scenario[F, Message]): Scenario[F, Message] =
      new Scenario[F, Message] {
        def fulfill(messages: Stream[F, Message]): Stream[F, (Message, Stream[F, Message])] =
          messages.head.flatMap { m =>
            if (p(m)) self.fulfill(Stream(m) ++ messages)
            else fn(m).fulfill(messages)
          }
      }

    def or[B](other: => Scenario[F, B]): Scenario[F, Either[Message, B]] =
      new Scenario[F, Either[Message, B]] {
        def fulfill(messages: Stream[F, Message]): Stream[F, (Either[Message, B], Stream[F, Message])] =
          messages.head.flatMap { m =>
            if (p(m)) self.fulfill(Stream(m) ++ messages).map { case (a, rest) => Left(a) -> rest }
            else other.fulfill(Stream(m) ++ messages).map { case (a, rest) => Right(a) -> rest }
          }
      }
  }

  final case class Action[F[_], A](fa: F[A]) extends Scenario[F, A] {
    def fulfill(messages: Stream[F, Message]): Stream[F, (A, Stream[F, Message])] =
      Stream.eval(fa).map(_ -> messages)
  }

  val greetings: Scenario[IO, Unit] =
    for {
      _ <- Receive[IO](_.startsWith("start"))
      _ <- Action(IO(println("ok, let's go")))
      _ <- Expect[IO](_ == "1").recover(_ => IO(println("Please provide 1")))
      _ <- Action(IO(println("Done.")))
    } yield ()

  def run(args: List[String]): IO[ExitCode] =
    Stream
      .repeatEval(IO(StdIn.readLine()))
      .through(greetings)
      .compile.drain.as(ExitCode.Success)

}
