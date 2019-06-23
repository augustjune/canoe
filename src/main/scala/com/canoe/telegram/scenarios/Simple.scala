package com.canoe.telegram.scenarios

import cats.effect.{ExitCode, IO, IOApp}
import cats.instances.string._
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

  final case class Expect[F[_]](p: Message => Boolean) extends Scenario[F, Message] {
    def fulfill(messages: Stream[F, Message]): Stream[F, (Message, Stream[F, Message])] = {
      val s = messages.dropWhile(!p(_))
      s.head.map(_ -> s.tail)
    }
  }

  final case class Action[F[_], A](fa: F[A]) extends Scenario[F, A] {
    def fulfill(messages: Stream[F, Message]): Stream[F, (A, Stream[F, Message])] =
      Stream.eval(fa).map(_ -> messages)
  }

  object Scenario {
    def expect[F[_]](pf: PartialFunction[Message, Boolean]): Scenario[F, Message] =
      Expect(m => pf.applyOrElse(m, (_: Message) => false))

    def expect[F[_]](p: Message => Boolean): Scenario[F, Message] =
      Expect(p)

    def eval[F[_], A](fa: F[A]): Scenario[F, A] =
      Action(fa)
  }


  val greetings: Scenario[IO, String] =
    for {
      _ <- Scenario.expect(_.startsWith("Hello"))
      _ <- Scenario.eval(IO(println("Found 'Hello'")))
      m2 <- Scenario.expect(_ => true)
      a <- Scenario.eval[IO, String](IO(s"Hello, master $m2"))
    } yield a

  def run(args: List[String]): IO[ExitCode] =
    Stream("This", "that", "Hello", "August")
      .through(greetings)
      .showLinesStdOut.compile.drain.map(_ => ExitCode.Success)

}
