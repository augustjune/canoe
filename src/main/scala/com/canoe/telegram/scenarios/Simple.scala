package com.canoe.telegram.scenarios

import cats.effect.concurrent.Ref
import cats.{Applicative, Id}
import cats.effect.{Concurrent, ExitCode, IO, IOApp, Sync}
import cats.syntax.all._
import fs2.concurrent.Broadcast
import fs2.{Pipe, Pull, Stream}

import scala.io.StdIn
import scala.util.{Failure, Random, Success, Try}

object Simple extends IOApp {

  type Message = String

  def chat(m: Message): String = m.takeWhile(_ != '.')

  def log[F[_] : Sync, A](id: String)(s: A): F[Unit] =
    Sync[F].delay(println(s"$id: $s"))

  sealed abstract class ChatScenario[F[_], A] extends Pipe[F, Message, A] {
    self =>

    def apply(messages: Stream[F, Message]): Stream[F, A] =
      fulfill(messages).map(_._1)

    def fulfill(messages: Stream[F, Message]): Stream[F, (A, Stream[F, Message])]

    def map[B](fn: A => B): ChatScenario[F, B] = new ChatScenario[F, B] {
      def fulfill(messages: Stream[F, Message]): Stream[F, (B, Stream[F, Message])] =
        self.fulfill(messages).map { case (a, rest) => (fn(a), rest) }
    }

    def flatMap[B](fn: A => ChatScenario[F, B]): ChatScenario[F, B] = {
      new ChatScenario[F, B] {
        def fulfill(messages: Stream[F, Message]): Stream[F, (B, Stream[F, Message])] =
          self.fulfill(messages).flatMap { case (a, rest) => fn(a).fulfill(rest) }
      }
    }
  }

  object ChatScenario {
    private def fullPredicate[A](pf: PartialFunction[A, Boolean]): A => Boolean =
      pf.applyOrElse(_, (_: A) => false)

    // ToDo - make builder methods return common abstraction i.e. Scenario
    //  (currently undesirable because of Expect methods, such as recover)
    //    def receive[F[_]](pf: PartialFunction[Message, Boolean]): Receive[F] =
    //      Receive(fullPredicate(pf))
    //
    def receive[F[_]](p: Message => Boolean): Receive[F] =
      Receive(p)

    def expect[F[_]](pf: PartialFunction[Message, Boolean]): Expect[F] =
      Expect(fullPredicate(pf))

    def expect[F[_]](p: Message => Boolean): Expect[F] =
      Expect(p)

    def execute[F[_], A](fa: F[A]): Action[F, A] =
      Action(fa)

    def pure[F[_] : Applicative, A](a: A): ChatScenario[F, A] =
      execute(a.pure[F])

    def pure[A](a: A): ChatScenario[Id, A] =
      execute[Id, A](a)

    def unit[F[_] : Applicative]: ChatScenario[F, Unit] =
      pure(())

    def end[F[_], A]: ChatScenario[F, A] =
      new ChatScenario[F, A] {
        def fulfill(messages: Stream[F, Message]): Stream[F, (A, Stream[F, Message])] = Stream.empty
      }
  }

  final case class Receive[F[_]](p: Message => Boolean) extends ChatScenario[F, Message] {
    self =>

    def fulfill(messages: Stream[F, Message]): Stream[F, (Message, Stream[F, Message])] = {

      def go(input: Stream[F, Message]): Pull[F, (Message, Stream[F, Message]), Unit] = {
        input.dropWhile(!p(_)).pull.uncons1.flatMap {
          case Some((m, rest)) =>
            Pull.output1(m -> rest) >> go(rest)

          case None => Pull.done
        }
      }

      go(messages).stream
    }
  }

  final case class Expect[F[_]](p: Message => Boolean) extends ChatScenario[F, Message] {
    self =>

    def fulfill(messages: Stream[F, Message]): Stream[F, (Message, Stream[F, Message])] =
      messages.head
        .filter(p)
        .map(_ -> messages)

    def recover[B](fn: Message => F[B]): ChatScenario[F, Message] = new ChatScenario[F, Message] {
      def fulfill(messages: Stream[F, Message]): Stream[F, (Message, Stream[F, Message])] =
        messages.head.flatMap { m =>
          if (p(m)) self.fulfill(messages.cons1(m))
          else Stream.eval(fn(m)) *> self.fulfill(messages)
        }
    }

    def recoverWith(fn: Message => ChatScenario[F, Message]): ChatScenario[F, Message] =
      new ChatScenario[F, Message] {
        def fulfill(messages: Stream[F, Message]): Stream[F, (Message, Stream[F, Message])] =
          messages.head.flatMap { m =>
            if (p(m)) self.fulfill(messages.cons1(m))
            else fn(m).fulfill(messages)
          }
      }

    def or[B](other: => ChatScenario[F, B]): ChatScenario[F, Either[Message, B]] =
      new ChatScenario[F, Either[Message, B]] {
        def fulfill(messages: Stream[F, Message]): Stream[F, (Either[Message, B], Stream[F, Message])] =
          messages.head.flatMap { m =>
            if (p(m)) self.fulfill(messages.cons1(m)).map { case (a, rest) => Left(a) -> rest }
            else other.fulfill(messages.cons1(m)).map { case (a, rest) => Right(a) -> rest }
          }
      }
  }

  final case class Action[F[_], A](fa: F[A]) extends ChatScenario[F, A] {
    def fulfill(messages: Stream[F, Message]): Stream[F, (A, Stream[F, Message])] =
      Stream.eval(fa).map(_ -> messages)
  }

  val start: ChatScenario[IO, Unit] =
    for {
      m1 <- Receive[IO](_.contains("start"))
      _ <- Action(IO(println(s"ok, let's go ${chat(m1)}")))
      m <- Expect[IO](_.contains("end"))
      _ <- Action(IO(println(s"Done $m")))
    } yield ()

  val greetings: ChatScenario[IO, Unit] =
    for {
      m1 <- Receive[IO](_.contains("Hello"))
      _ <- Action(IO(println(s"${chat(m1)}, how are you doing?")))
      m2 <- Expect[IO](_.contains("fine")) or Expect[IO](_.contains("bad"))
      _ <- m2 match {
        case Left(fine) => Action(IO(println("Oh, I'm so happy for you")))
        case Right(bad) => Action(IO(println("Oh, I'm sorry. Is there something I can do for you?")))
      }
    } yield ()

  def forkThrough[F[_] : Concurrent, A](stream: Stream[F, A], pipes: Pipe[F, A, Any]*): Stream[F, A] =
    stream.through(Broadcast.through((identity: Pipe[F, A, A]) :: pipes.toList.map(_.andThen(_.drain)): _*))

  def runScenario[F[_] : Concurrent, A](scenario: ChatScenario[F, A], messages: Stream[F, Message]): Stream[F, A] = {

    val filterByFirst: Pipe[F, Message, Message] =
      _.pull.peek1.flatMap {
        case Some((m, rest)) => rest.filter(chat(_) == chat(m)).pull.echo
        case None => Pull.done
      }.stream

    def go(input: Stream[F, Message], ids: Ref[F, Set[String]]): Pull[F, A, Unit] =
      input.pull.peek1.flatMap {
        case Some((m, rest)) =>
          Pull.eval(ids.get.map(set => set.contains(chat(m)))).flatMap {
            case true => // contains
              go(rest.tail, ids)

            case false => // doesn't contain
              //              Pull.eval(Sync[F].delay(println("Adding new id"))) >>
              Pull.eval(ids.update(_ + chat(m))) >>
                go(forkThrough(rest, scenario compose filterByFirst).tail, ids)
          }

        case None => Pull.done
      }

    Stream.eval(Ref.of(Set.empty[String])).flatMap(ids => go(messages, ids).stream)
  }

  import scala.concurrent.duration._

  val repeat: ChatScenario[IO, Unit] =
    for {
      m <- Expect(_ => true)
      _ <-
        if (m.contains("stop")) Action(IO(println("Ok, that's all")))
        else Action(IO(println(m))).flatMap(_ => repeat)
    } yield ()

  val mock: ChatScenario[IO, Unit] =
    for {
      start <- Receive[IO](_.contains("start"))
      _ <- Try(chat(start).toInt) match {
        case Success(i) if i % 100 == 0 => Action(IO(println(s"Starting mocking ${chat(start)}")))
        case _ => Action(IO.unit)
      }
      _ <- repeat
    } yield ()

  val consoleIn: Stream[IO, Message] = Stream.repeatEval(IO(StdIn.readLine()))

  val n = 2000

  val pre: Stream[IO, Message] =
    Stream
      .range(0, n).covary[IO]
      .map(i => s"$i.start")

  def run(args: List[String]): IO[ExitCode] =
  //    consoleIn.through(greetings)
    runScenario(mock, pre ++ consoleIn.flatMap(s => Stream.repeatEval(IO(Random.nextInt(n))).map(i => s"$i.$s").take(20)))
      .compile.toList.map(l => println(s"#${l.size}"))
      .as(ExitCode.Success)

}
