package com.canoe.telegram.scenarios

import cats.effect.IO
import cats.{Applicative, FlatMap, Id, Monad}
import fs2.{Pipe, Pull, Pure, Stream}
import cats.syntax.all._

object ScenarioAlgebra extends App {

  import Scenario._
  sealed trait Scenario[F[_], I, +O] extends Pipe[F, I, O] {
    def apply(input: Stream[F, I]): Stream[F, O] =
      loop(this, input, Nil).collect {
        case (Matched(o), _) => o
      }

    def flatMap[O2](fn: O => Scenario[F, I, O2]): Scenario[F, I, O2] =
      Bind(this, fn)

    def map[O2](fn: O => O2): Scenario[F, I, O2] = Mapped(this, fn)

    def cancelOn(p: I => Boolean): Scenario[F, I, O] = Cancellable(this, p)

    def tolerateN[A](n: Int)(fn: I => F[A]): Scenario[F, I, O] =
      Tolerate(this, Some(n), fn)

    def tolerate[A](fn: I => F[A]): Scenario[F, I, O] = tolerateN(1)(fn)

    def tolerateAll[A](fn: I => F[A]): Scenario[F, I, O] =
      Tolerate(this, None, fn)
  }

  final case class Eval[F[_], I, A](fa: F[A]) extends Scenario[F, I, A]

  final case class Next[F[_], A](p: A => Boolean) extends Scenario[F, A, A]

  final case class Start[F[_], A](p: A => Boolean) extends Scenario[F, A, A]

  final case class Bind[F[_], I, O1, O2](scenario: Scenario[F, I, O1],
                                         fn: O1 => Scenario[F, I, O2])
      extends Scenario[F, I, O2]

  final case class Mapped[F[_], I, O1, O2](scenario: Scenario[F, I, O1],
                                           fn: O1 => O2)
      extends Scenario[F, I, O2]

  final case class Cancellable[F[_], I, O](scenario: Scenario[F, I, O],
                                           cancelOn: I => Boolean)
      extends Scenario[F, I, O]

  final case class Tolerate[F[_], I, O, A](scenario: Scenario[F, I, O],
                                           limit: Option[Int],
                                           fn: I => F[A])
      extends Scenario[F, I, O]

  object Scenario {

    def eval[F[_], I, A](fa: F[A]): Scenario[F, I, A] = Eval(fa)

    def start[F[_], I](p: I => Boolean): Scenario[F, I, I] = Start(p)

    def next[F[_], I](p: I => Boolean): Scenario[F, I, I] = Next(p)

    sealed trait Result[+E, +A] {
      def map[B](f: A => B): Result[E, B] = this match {
        case Matched(a)          => Matched(f(a))
        case same @ Missed(_)    => same
        case same @ Cancelled(_) => same
      }
    }

    case class Matched[A](a: A) extends Result[Nothing, A]
    case class Missed[E](message: E) extends Result[E, Nothing]
    case class Cancelled[E](message: E) extends Result[E, Nothing]

    def loop[F[_], I, O](
      scenario: Scenario[F, I, O],
      input: Stream[F, I],
      cancelTokens: List[I => Boolean]
    ): Stream[F, (Result[I, O], Stream[F, I])] = {
      scenario match {
        case Eval(fa) =>
          Stream.eval(fa).map(o => Matched(o) -> input)

        case Start(p: (I => Boolean)) =>
          def go(
            in: Stream[F, I]
          ): Pull[F, (Result[I, O], Stream[F, I]), Unit] =
            in.dropWhile(!p(_)).pull.uncons1.flatMap {
              case Some((m, rest)) =>
                Pull.output1(Matched(m.asInstanceOf[O]) -> rest) >> go(rest)

              case None => Pull.done
            }

          go(input).stream

        case Next(p) =>
          def go(
            in: Stream[F, I]
          ): Pull[F, (Result[I, O], Stream[F, I]), Unit] =
            in.pull.uncons1.flatMap {
              case Some((m, rest)) =>
                if (cancelTokens.exists(_(m)))
                  Pull.output1(Cancelled(m) -> rest)
                else if (p(m)) Pull.output1(Matched(m.asInstanceOf[O]) -> rest)
                else Pull.output1(Missed(m) -> rest)

              case None => Pull.done
            }

          go(input).stream

        case Cancellable(scenario, p) =>
          loop(scenario, input, p :: cancelTokens)

        case Tolerate(scenario, limit, fn) =>
          limit match {
            case Some(n) if n <= 0 =>
              loop(scenario, input, cancelTokens)

            case _ =>
              loop(scenario, input, cancelTokens).flatMap {
                case (Cancelled(m), rest) => Stream(Cancelled(m) -> rest)
                case (Missed(m), rest) =>
                  Stream.eval(fn(m)) >> loop(
                    Tolerate(scenario, limit.map(_ - 1), fn),
                    rest,
                    cancelTokens
                  )
                case matched => Stream(matched)
              }
          }

        case Bind(prev, fn) =>
          loop(prev, input, cancelTokens).flatMap {
            case (result, rest) =>
              result match {
                case Matched(a)         => loop(fn(a), rest, cancelTokens)
                case Missed(message)    => Stream(Missed(message) -> rest)
                case Cancelled(message) => Stream(Cancelled(message) -> rest)
              }
          }

        case Mapped(prev, fn) =>
          loop(prev, input, cancelTokens).map {
            case (result, rest) => result.map(fn) -> rest
          }
      }
    }

    implicit def monadInstance[F[_]: Applicative, I]: Monad[Scenario[F, I, ?]] =
      new Monad[Scenario[F, I, ?]] {
        def pure[A](x: A): Scenario[F, I, A] = Eval(Applicative[F].pure(x))

        def flatMap[A, B](
          scenario: Scenario[F, I, A]
        )(fn: A => Scenario[F, I, B]): Scenario[F, I, B] =
          scenario.flatMap(fn)

        override def map[A, B](
          scenario: Scenario[F, I, A]
        )(f: A => B): Scenario[F, I, B] =
          scenario.map(f)

        def tailRecM[A, B](
          a: A
        )(f: A => Scenario[F, I, Either[A, B]]): Scenario[F, I, B] =
          flatMap(f(a)) {
            case Left(a)  => tailRecM(a)(f)
            case Right(b) => Eval(Applicative[F].pure(b))
          }
      }
  }

  implicit class IOStreamOps[A](stream: Stream[IO, A]) {
    def toList: List[A] = stream.compile.toList.unsafeRunSync()

    def size: Int = toList.size
  }

  val scenario: Scenario[IO, String, String] =
    for {
      m1 <- Scenario.start[IO, String](_.endsWith("start"))
      _ <- Scenario.eval(IO { println("start matched") })
      m2 <- Scenario
        .next[IO, String](_.endsWith("end"))
        .tolerateAll(
          s =>
            IO(
              println(
                s"Message: '$s' is unacceptable. Next message should end with 'end'"
              )
          )
        )
      _ <- Scenario.eval(IO { println("end matched") })
    } yield s"$m1 - $m2"

  val input =
    Stream("1.start", "dasd", "ogo", "gg", "1.end", "2.start", "2.end")

  println(input.through(scenario.cancelOn(_ == "/cancel")).toList)
}
