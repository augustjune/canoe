package com.canoe.telegram.scenarios

import cats.effect.{ExitCode, IO, IOApp}
import cats.free.Free
import cats.free.Free.liftF
import cats.~>
import cats.syntax.all._
import fs2.Stream

import scala.io.StdIn

object FreeScenario extends IOApp {

  type Message = String

  sealed trait Scenario[F[_], A]

  case class Receive[F[_]](p: Message => Boolean) extends Scenario[F, Message]

  case class Expect[F[_]](p: Message => Boolean) extends Scenario[F, Message]

  case class Action[F[_], A](f: F[A]) extends Scenario[F, A]

  type FreeSc[F[_], A] = Free[Scenario[F, ?], A]

  def receive[F[_]](p: Message => Boolean): FreeSc[F, Message] =
    liftF[Scenario[F, ?], Message](Receive[F](p))

  def expect[F[_]](p: Message => Boolean): FreeSc[F, Message] =
    liftF[Scenario[F, ?], Message](Expect[F](p))

  def action[F[_], A](fa: F[A]): FreeSc[F, A] =
    liftF[Scenario[F, ?], A](Action(fa))

  def transformation(input: Stream[IO, Message]): Scenario[IO, ?] ~> Stream[IO, ?] =
    new (Scenario[IO, ?] ~> Stream[IO, ?]) {
      def apply[A](scenario: Scenario[IO, A]): Stream[IO, A] = {
        scenario match {
          case Receive(p) => input.filter(p).asInstanceOf[Stream[IO, A]]
          case Expect(p) => input.head.filter(p).asInstanceOf[Stream[IO, A]]
          case Action(fa) => Stream.eval(fa)
        }
      }
    }

  val scenario: Free[Scenario[IO, ?], Unit] = for {
    m <- receive[IO](_.startsWith("hi"))
    _ <- action(IO(println("oh, hi there")))
    name <- expect[IO](_.head.isUpper)
    _ <- action(IO(println("general kenobi?!")))
  } yield ()

  val consoleIn: Stream[IO, Message] = Stream.repeatEval(IO(StdIn.readLine()))

  def run(args: List[String]): IO[ExitCode] =
    scenario.foldMap(transformation(consoleIn))
    .compile.drain.as(ExitCode.Success)
}
