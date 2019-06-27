package com.canoe.telegram.scenarios

import cats.effect.{Concurrent, ExitCode, IO, IOApp, Sync}
import fs2.Stream
import fs2.concurrent.Topic

import scala.io.StdIn

object Simple extends IOApp {

  type Message = String

  def chat(m: Message): String = m.take(3) + m.length

  def log[F[_] : Sync, A](id: String)(s: A): F[A] = Sync[F].delay {
    println(s"$id: $s")
    s
  }

  def evalLog[F[_] : Concurrent, A](id: String)(s: A): F[A] = Concurrent[F].delay {
    println(s"$id: $s")
    s
  }

  implicit class MessageScenario[F[_]: Concurrent, A <: Message](val scenario: Scenario[F, A]) {
    def withinChat: Scenario[F, A] = new Scenario[F, A] {
      def fulfill(messages: Stream[F, Message]): Stream[F, (A, Stream[F, Message])] =
        scenario.fulfill(messages).map { case (a, rest) => a -> rest.filter(chat(_) == chat(a)) }
    }
  }

  object Scenario {
    private def fullPredicate[A](pf: PartialFunction[A, Boolean]): A => Boolean =
      pf.applyOrElse(_, (_: A) => false)

    //    def receive[F[_]](pf: PartialFunction[Message, Boolean]): Scenario[F, Message] =
    //      Receive(fullPredicate(pf))
    //
    //    def receive[F[_]](p: Message => Boolean): Scenario[F, Message] =
    //      Receive(p)
    //
    //    def expect[F[_]](pf: PartialFunction[Message, Boolean]): Scenario[F, Message] =
    //      Expect(fullPredicate(pf))
    //
    //    def expect[F[_]](p: Message => Boolean): Scenario[F, Message] =
    //      Expect(p)
    //
    //    def execute[F[_], A](fa: F[A]): Scenario[F, A] =
    //      Action(fa)
    //
    //    def pure[F[_] : Applicative, A](a: A): Scenario[F, A] =
    //      execute(a.pure[F])
    //
    //    def pure[A](a: A): Scenario[Id, A] =
    //      execute[Id, A](a)
    //
    //    def unit[F[_] : Applicative]: Scenario[F, Unit] =
    //      pure(())

    def end[F[_]: Concurrent, A]: Scenario[F, A] =
      new Scenario[F, A] {
        def fulfill(messages: Stream[F, Message]): Stream[F, (A, Stream[F, Message])] = Stream.empty
      }
  }

  sealed abstract class Scenario[F[_]: Concurrent, A] extends (Stream[F, Message] => Stream[F, A]) {
    self =>

    def apply(messages: Stream[F, Message]): Stream[F, A] =
      fulfill(messages).map(_._1)

    def fulfill(messages: Stream[F, Message]): Stream[F, (A, Stream[F, Message])]

    def map[B](fn: A => B): Scenario[F, B] = new Scenario[F, B] {
      def fulfill(messages: Stream[F, Message]): Stream[F, (B, Stream[F, Message])] =
        self.fulfill(messages).map { case (a, rest) => (fn(a), rest) }
    }

    def or[B](other: => Scenario[F, B]): Scenario[F, Either[A, B]] =
      new Scenario[F, Either[A, B]] {
        def fulfill(messages: Stream[F, Message]): Stream[F, (Either[A, B], Stream[F, Message])] = {
          (self.fulfill(messages).map { case (a, rest) => Left(a) -> rest } ++
            other.fulfill(messages).map { case (b, rest) => Right(b) -> rest }).head
        }
      }

    def recoverWith(fn: Message => Scenario[F, A]): Scenario[F, A] =
      new Scenario[F, A] {
        def fulfill(messages: Stream[F, Message]): Stream[F, (A, Stream[F, Message])] = {
          val primary = self.fulfill(messages)
          val secondary = messages.head.map(fn).flatMap(sc => sc.fulfill(messages))
          (primary ++ secondary).head
        }
      }

    def recover[B](fn: Message => F[B]): Scenario[F, A] =
      new Scenario[F, A] {
        def fulfill(messages: Stream[F, Message]): Stream[F, (A, Stream[F, Message])] = {
          val primary = self.fulfill(messages)
          val secondary = messages.evalMap(log[F, Message]("second")).head.evalMap(fn).flatMap(_ => self.fulfill(messages))
          (primary ++ secondary).head
        }
      }

    def flatMap[B](fn: A => Scenario[F, B]): Scenario[F, B] = {
      new Scenario[F, B] {
        def fulfill(messages: Stream[F, Message]): Stream[F, (B, Stream[F, Message])] =
          self.fulfill(messages).flatMap { case (a, rest) => fn(a).fulfill(rest) }
      }
    }
  }

  // Fixed
  final case class Receive[F[_] : Concurrent](p: Message => Boolean) extends Scenario[F, Message] {
    def fulfill(messages: Stream[F, Message]): Stream[F, (Message, Stream[F, Message])] = {
      messages
        .evalMap(log[F, Message]("receive - before predicate"))
        .filter(p)
        .evalMap(log[F, Message]("receive - after predicate"))
        .map(_ -> messages)
    }
  }

  // Good
  final case class Expect[F[_] : Concurrent](p: Message => Boolean) extends Scenario[F, Message] {
    def fulfill(messages: Stream[F, Message]): Stream[F, (Message, Stream[F, Message])] = {
      messages.head
        .evalMap(log[F, Message]("expect - before predicate"))
        .filter(p)
        .evalMap(log[F, Message]("expect - after predicate"))
        .map(_ -> messages)
    }
  }

  // Good
  final case class Action[F[_] : Concurrent, A](fa: F[A]) extends Scenario[F, A] {
    def fulfill(messages: Stream[F, Message]): Stream[F, (A, Stream[F, Message])] =
      Stream.eval(fa).map(_ -> messages)
  }

  val greetings: Scenario[IO, Unit] =
    for {
      _ <- Receive[IO](_.startsWith("start"))
      _ <- Action(IO(println("ok, let's go")))
      _ <- Expect[IO](_ == "1")
        .recover(_ => IO(println("Please provide 1")))
      _ <- Action(IO(println("Done.")))
    } yield ()

  def run(args: List[String]): IO[ExitCode] =
    for {
      topic <- Topic[IO, String]("")
      _ <- Stream
        .repeatEval(IO(StdIn.readLine()))
        .through(greetings)
        .compile.drain
    } yield ExitCode.Success
}
