package canoe.scenarios

import canoe.scenarios.Episode._
import cats.instances.list._
import cats.syntax.all._
import cats.{Applicative, Monad}
import fs2.{INothing, Pipe, Pull, Stream}

sealed trait Episode[F[_], -I, +O] extends Pipe[F, I, O] {

  def apply(input: Stream[F, I]): Stream[F, O] =
    find(this, input, Nil).collect {
      case (Matched(o), _) => o
    }

  def flatMap[I2 <: I, O2](fn: O => Episode[F, I2, O2]): Episode[F, I2, O2] =
    Bind(this, fn)

  def >>[I2 <: I, O2](e2: => Episode[F, I2, O2]): Episode[F, I2, O2] =
    flatMap(_ => e2)

  def map[O2](fn: O => O2): Episode[F, I, O2] = Mapped(this, fn)

  def cancelOn[I2 <: I](p: I2 => Boolean): Episode[F, I2, O] =
    Cancellable(this, p, None)

  /**
    * @param p Predicate which determines what input value causes cancellation
    * @param cancellation Function which result is going to be evaluated during the cancellation
    */
  def cancelWith[I2 <: I](
    p: I2 => Boolean
  )(cancellation: I2 => F[Unit]): Episode[F, I2, O] =
    Cancellable(this, p, Some(cancellation))

  def tolerateN[I2 <: I](n: Int)(fn: I2 => F[Unit]): Episode[F, I2, O] =
    Tolerate(this, Some(n), fn)

  def tolerate[I2 <: I](fn: I2 => F[Unit]): Episode[F, I2, O] =
    tolerateN(1)(fn)

  def tolerateAll[I2 <: I](fn: I2 => F[Unit]): Episode[F, I2, O] =
    Tolerate(this, None, fn)
}

private final case class Eval[F[_], I, A](fa: F[A]) extends Episode[F, I, A]

private final case class Next[F[_], A](p: A => Boolean) extends Episode[F, A, A]

private final case class First[F[_], A](p: A => Boolean) extends Episode[F, A, A]

private final case class Bind[F[_], I, O1, O2](episode: Episode[F, I, O1], fn: O1 => Episode[F, I, O2])
    extends Episode[F, I, O2]

private final case class Mapped[F[_], I, O1, O2](episode: Episode[F, I, O1], fn: O1 => O2) extends Episode[F, I, O2]

private final case class Cancellable[F[_], I, O](
  episode: Episode[F, I, O],
  cancelOn: I => Boolean,
  finalizer: Option[I => F[Unit]]
) extends Episode[F, I, O]

private final case class Tolerate[F[_], I, O](episode: Episode[F, I, O], limit: Option[Int], fn: I => F[Unit])
    extends Episode[F, I, O]

object Episode {

  def eval[F[_], I, A](fa: F[A]): Episode[F, I, A] = Eval(fa)

  def first[F[_], I](p: I => Boolean): Episode[F, I, I] = First(p)

  def next[F[_], I](p: I => Boolean): Episode[F, I, I] = Next(p)

  implicit def monadInstance[F[_]: Applicative, I]: Monad[Episode[F, I, *]] =
    new Monad[Episode[F, I, *]] {
      def pure[A](x: A): Episode[F, I, A] = Eval(Applicative[F].pure(x))

      def flatMap[A, B](
        episode: Episode[F, I, A]
      )(fn: A => Episode[F, I, B]): Episode[F, I, B] =
        episode.flatMap(fn)

      override def map[A, B](
        episode: Episode[F, I, A]
      )(f: A => B): Episode[F, I, B] =
        episode.map(f)

      def tailRecM[A, B](
        a: A
      )(f: A => Episode[F, I, Either[A, B]]): Episode[F, I, B] =
        flatMap(f(a)) {
          case Left(a)  => tailRecM(a)(f)
          case Right(b) => Eval(Applicative[F].pure(b))
        }
    }

  sealed private trait Result[+E, +A] {
    def map[B](f: A => B): Result[E, B] = this match {
      case Matched(a)          => Matched(f(a))
      case same @ Missed(_)    => same
      case same @ Cancelled(_) => same
    }
  }

  private final case class Matched[A](a: A) extends Result[Nothing, A]
  private final case class Missed[E](message: E) extends Result[E, Nothing]
  private final case class Cancelled[E](message: E) extends Result[E, Nothing]

  private def find[F[_], I, O](
    episode: Episode[F, I, O],
    input: Stream[F, I],
    cancelTokens: List[(I => Boolean, Option[I => F[Unit]])]
  ): Stream[F, (Result[I, O], Stream[F, I])] =
    episode match {
      case Eval(fa) =>
        Stream.eval(fa).map(o => Matched(o) -> input)

      case First(p: (I => Boolean)) =>
        def go(in: Stream[F, I]): Pull[F, (Result[I, O], Stream[F, I]), Unit] =
          in.dropWhile(!p(_)).pull.uncons1.flatMap {
            case Some((m, rest)) =>
              Pull.output1(Matched(m.asInstanceOf[O]) -> rest) >> go(rest)

            case None => Pull.done
          }

        go(input).stream

      case Next(p) =>
        def go(in: Stream[F, I]): Pull[F, (Result[I, O], Stream[F, I]), Unit] =
          in.pull.uncons1.flatMap {
            case Some((m, rest)) =>
              cancelTokens.collect { case (p, f) if p(m) => f } match {
                case Nil =>
                  if (p(m)) Pull.output1(Matched(m.asInstanceOf[O]) -> rest)
                  else Pull.output1(Missed(m) -> rest)

                case nonEmpty =>
                  nonEmpty
                    .collect { case Some(f) => f }
                    .traverse[Pull[F, INothing, *], Unit](f => Pull.eval(f(m))) >>
                    Pull.output1(Cancelled(m) -> rest)
              }
            case None => Pull.done
          }

        go(input).stream

      case Cancellable(episode, p, f) =>
        find(episode, input, (p, f) :: cancelTokens)

      case Tolerate(episode, limit, fn) =>
        limit match {
          case Some(n) if n <= 0 =>
            find(episode, input, cancelTokens)

          case _ =>
            find(episode, input, cancelTokens).flatMap {
              case (Cancelled(m), rest) => Stream(Cancelled(m) -> rest)
              case (Missed(m), rest) =>
                Stream.eval(fn(m)) >> find(
                  Tolerate(episode, limit.map(_ - 1), fn),
                  rest,
                  cancelTokens
                )
              case matched => Stream(matched)
            }
        }

      case Bind(prev, fn) =>
        find(prev, input, cancelTokens).flatMap {
          case (result, rest) =>
            result match {
              case Matched(a)         => find(fn(a), rest, cancelTokens)
              case Missed(message)    => Stream(Missed(message) -> rest)
              case Cancelled(message) => Stream(Cancelled(message) -> rest)
            }
        }

      case Mapped(prev, fn) =>
        find(prev, input, cancelTokens).map {
          case (result, rest) => result.map(fn) -> rest
        }
    }
}
