package canoe.api

import canoe.api.matching.Episode
import canoe.models.messages.TelegramMessage
import canoe.syntax.Expect
import cats.syntax.applicativeError._
import cats.{ApplicativeError, Monad, MonadError, StackSafeMonad}
import fs2.Pipe

/**
  * Description of an interaction between two sides,
  * where generally one is the application (bot) and the other is Telegram user.
  *
  * Particular interaction is coherent with some scenario as long as it matches the description
  * (i.e. every step of the interaction is the same as described in scenario).
  *
  * `Scenario` forms a monad in `A` with `pure` and `flatMap`.
  */
final class Scenario[F[_], A] private (private val ep: Episode[F, TelegramMessage, A]) extends AnyVal {

  def pipe: Pipe[F, TelegramMessage, A] = ep.matching

  def flatMap[B](fn: A => Scenario[F, B]): Scenario[F, B] =
    new Scenario[F, B](ep.flatMap(fn(_).ep))

  def >>[B](s2: => Scenario[F, B]): Scenario[F, B] = flatMap(_ => s2)

  /** Monadic map to enable for-comprehensions without importing monad syntax */
  def map[B](fn: A => B): Scenario[F, B] = flatMap(fn.andThen(Scenario.pure))

  /**
    * @return Scenario which ignores the input element, which causes
    *         missed result, `n` time and evaluates `fn` for every such element
    */
  def tolerateN(n: Int)(fn: TelegramMessage => F[Unit]): Scenario[F, A] =
    new Scenario[F, A](Episode.Tolerate(ep, Some(n), fn))

  /** Alias for tolerateN(1) */
  def tolerate(fn: TelegramMessage => F[Unit]): Scenario[F, A] = tolerateN(1)(fn)

  /**
    * @return Scenario which ignores every input element which causes
    *         missed result and evaluates `fn` for every such element
    */
  def tolerateAll(fn: TelegramMessage => F[Unit]): Scenario[F, A] =
    new Scenario[F, A](Episode.Tolerate(ep, None, fn))

  /**
    * @return Scenario which is cancellable by the occurrence of input element, for which `expect` is defined,
    *         at any point after the scenario is started and before it is finished.
    */
  def cancelOn[Any](expect: Expect[Any]): Scenario[F, A] =
    new Scenario[F, A](Episode.Cancellable(ep, expect.isDefinedAt, None))

  /**
    * @param expect       Partial function which defines the domain of input values which cause cancellation
    * @param cancellation Function which result is going to be evaluated during the cancellation
    *
    * @return Scenario which is cancellable by the occurrence of input element described
    *         by predicate `p` at any point after the scenario is started and before it is finished,
    *         and evaluates `cancellation` when such element occurs.
    */
  def cancelWith[Any](expect: Expect[Any])(cancellation: TelegramMessage => F[Unit]): Scenario[F, A] =
    new Scenario[F, A](Episode.Cancellable(ep, expect.isDefinedAt, Some(cancellation)))

  /**
    * @return `this` or scenario which is result of `fn` if error occurs.
    */
  def handleErrorWith(fn: Throwable => Scenario[F, A]): Scenario[F, A] =
    new Scenario[F, A](Episode.Protected(ep, fn(_).ep))

  /**
    * @return Scenario which wraps successful result values in `Right` and raised errors in `Left`.
    */
  def attempt: Scenario[F, Either[Throwable, A]] =
    map(Right(_): Either[Throwable, A]).handleErrorWith(e => Scenario.pure(Left(e)))
}

object Scenario {

  /**
    * Defines the beginning of the scenario.
    *
    * Each input value from `pf` domain is going to be matched and transformed into `A` type value.
    */
  def start[F[_], A](pf: PartialFunction[TelegramMessage, A]): Scenario[F, A] =
    new Scenario[F, A](Episode.First(pf.isDefinedAt).map(pf))

  /**
    * Defines following step of the scenario.
    *
    * If the first elements belongs to the `pf` domain,
    * it is going to be matched and transformed into `A` type value.
    */
  def next[F[_], A](pf: PartialFunction[TelegramMessage, A]): Scenario[F, A] =
    new Scenario[F, A](Episode.Next(pf.isDefinedAt).map(pf))

  /**
    * Suspends an effectful value of type `A` into Scenario context.
    *
    * Generally used for describing bot part of a scenario
    * (e.g. sending messages, making calls to external APIs, etc.)
    */
  def eval[F[_], A](fa: F[A]): Scenario[F, A] =
    new Scenario[F, A](Episode.Eval(fa))

  /**
    * Lifts pure value to Scenario context
    */
  def pure[F[_], A](a: A): Scenario[F, A] =
    new Scenario[F, A](Episode.Pure(a))

  def done[F[_]]: Scenario[F, Unit] = pure(())

  implicit def monadErrorInstance[F[_]: ApplicativeError[*[_], Throwable]]: MonadError[Scenario[F, *], Throwable] =
    new MonadError[Scenario[F, *], Throwable] with StackSafeMonad[Scenario[F, *]] {
      def pure[A](a: A): Scenario[F, A] = Scenario.pure(a)

      def flatMap[A, B](scenario: Scenario[F, A])(fn: A => Scenario[F, B]): Scenario[F, B] =
        scenario.flatMap(fn)

      def raiseError[A](e: Throwable): Scenario[F, A] =
        Scenario.eval(e.raiseError[F, A])

      def handleErrorWith[A](scenario: Scenario[F, A])(fn: Throwable => Scenario[F, A]): Scenario[F, A] =
        scenario.handleErrorWith(fn)
    }

  implicit def monadInstance[F[_]]: Monad[Scenario[F, *]] =
    new StackSafeMonad[Scenario[F, *]] {
      def pure[A](a: A): Scenario[F, A] = Scenario.pure(a)

      def flatMap[A, B](scenario: Scenario[F, A])(fn: A => Scenario[F, B]): Scenario[F, B] =
        scenario.flatMap(fn)
    }
}
