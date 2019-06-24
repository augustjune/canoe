package com.canoe.telegram.scenarios

import cats.effect.{ExitCode, IO, IOApp}
import cats.syntax.all._
import cats.{Applicative, Id}
import fs2.Stream

object Simple extends IOApp {

  type Message = String

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
    def fulfill(messages: Stream[F, Message]): Stream[F, (Message, Stream[F, Message])] = {
      messages.filter(p).map(m => m -> messages.dropThrough(_ != m))
    }
  }

  final case class Expect[F[_]](p: Message => Boolean) extends Scenario[F, Message] {
    def fulfill(messages: Stream[F, Message]): Stream[F, (Message, Stream[F, Message])] = {
      messages.head.filter(p).map(_ -> messages.tail)
    }
  }

  final case class Action[F[_], A](fa: F[A]) extends Scenario[F, A] {
    def fulfill(messages: Stream[F, Message]): Stream[F, (A, Stream[F, Message])] =
      Stream.eval(fa).map(_ -> messages)
  }

  object Scenario {
    private def fullPredicate[A](pf: PartialFunction[A, Boolean]): Function1[A, Boolean] =
      pf.applyOrElse(_, (_: A) => false)

    def receive[F[_]](pf: PartialFunction[Message, Boolean]): Scenario[F, Message] =
      Receive(fullPredicate(pf))

    def receive[F[_]](p: Message => Boolean): Scenario[F, Message] =
      Receive(p)

    def expect[F[_]](pf: PartialFunction[Message, Boolean]): Scenario[F, Message] =
      Expect(fullPredicate(pf))

    def expect[F[_]](p: Message => Boolean): Scenario[F, Message] =
      Expect(p)

    def execute[F[_], A](fa: F[A]): Scenario[F, A] =
      Action(fa)
  }

  val recursive: Scenario[IO, Unit] =
    Scenario.execute(IO(println("i"))).flatMap(_ => recursive)

  val greetings: Scenario[IO, Unit] =
    for {
      m1 <- Scenario.receive(_.startsWith("Hello"))
      _ <- Scenario.execute(IO(println(s"Found '$m1'")))
      name <- Scenario.expect(_.head.isUpper)
      _ <- Scenario.execute[IO, Unit](IO(println(s"Hello, master $name")))
    } yield ()

  def run(args: List[String]): IO[ExitCode] =
    Stream("Some", "Hello", "august", "August")
//      .evalMap(s => IO {println(s"Evaluating '$s'"); s })
      .through(greetings)
      .compile.toList
      .flatMap(l => IO(println(l.size)))
      .map(_ => ExitCode.Success)

}
