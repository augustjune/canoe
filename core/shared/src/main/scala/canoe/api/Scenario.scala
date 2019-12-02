package canoe.api

import canoe.api.matching.Episode
import canoe.models.messages.TelegramMessage
import canoe.syntax.Expect
import cats.arrow.FunctionK
import cats.{~>, ApplicativeError, MonadError, StackSafeMonad}
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
final class Scenario[F[_], +A] private (private val ep: Episode[F, TelegramMessage, A]) extends AnyVal {
  /**
    * Pipe which produces a value of type `A` evaluated in `F` effect
    * as a result of the successful interaction matching this description.
    * If an unhandled error result was encountered during the interaction, it will be raised here.
    */
  def pipe(implicit F: ApplicativeError[F, Throwable]): Pipe[F, TelegramMessage, A] =
    ep.matching

  /**
    * Chains this scenario with the one produced by applying `fn` to the result of this scenario.
    */
  def flatMap[B](fn: A => Scenario[F, B]): Scenario[F, B] =
    new Scenario[F, B](ep.flatMap(fn(_).ep))

  /**
    * Lazy `flatMap` which ignores the result of the first scenario.
    */
  def >>[B](s2: => Scenario[F, B]): Scenario[F, B] = flatMap(_ => s2)

  /**
    * Maps successful result values using provided function `fn`.
    */
  def map[B](fn: A => B): Scenario[F, B] = flatMap(fn.andThen(Scenario.pure))

  /**
    * @return `this` or scenario which is result of `fn` if error occurs.
    */
  def handleErrorWith[A2 >: A](fn: Throwable => Scenario[F, A2]): Scenario[F, A2] =
    new Scenario[F, A2](Episode.Protected(ep, fn(_).ep))

  /**
    * @return Scenario which wraps successful result values in `Right` and raised errors in `Left`.
    */
  def attempt: Scenario[F, Either[Throwable, A]] =
    map(Right(_): Either[Throwable, A]).handleErrorWith(e => Scenario.pure(Left(e)))

  /**
    * @return Scenario which ignores input element which causes
    *         missed result, up to `n` times and evaluates `fn` for every such element.
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
    * @return Scenario which is cancellable by the occurrence of input element for which `expect` is defined,
    *         at any point after the scenario is started and before it is finished.
    */
  def cancelOn(expect: Expect[_]): Scenario[F, A] =
    new Scenario[F, A](Episode.Cancellable(ep, expect.isDefinedAt, None))

  /**
    * @param expect       Partial function which defines the domain of input values which cause cancellation
    * @param cancellation Function which result is going to be evaluated during the cancellation
    *
    * @return Scenario which is cancellable by the occurrence of an input element from `expect` domain
    *         at any point after the scenario is started and before it is finished,
    *         and evaluates `cancellation` when such element occurs.
    */
  def cancelWith(expect: Expect[_])(cancellation: TelegramMessage => F[Unit]): Scenario[F, A] =
    new Scenario[F, A](Episode.Cancellable(ep, expect.isDefinedAt, Some(cancellation)))

  /**
    * Maps effect type from `F` to `G` using the supplied transformation.
    *
    * Warning: this operation can result into StackOverflowError
    * if `this` is nested with a lot of `flatMap` operations.
    */
  def mapK[G[_]](f: F ~> G): Scenario[G, A] =
    new Scenario[G, A](ep.mapK(f))
}

object Scenario {
  /**
    * Describes the next expected input message.
    * 
    * Any input message from `pf` domain will be matched 
    * and transformed into a value of type `A`.
    */
  def expect[F[_], A](pf: PartialFunction[TelegramMessage, A]): Scenario[F, A] =
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
    * Lifts pure value to Scenario context.
    */
  def pure[F[_], A](a: A): Scenario[F, A] =
    new Scenario[F, A](Episode.Pure(a))

  /**
    * Unit value lifted to Scenario context with effect `F`.
    */
  def done[F[_]]: Scenario[F, Unit] = pure(())

  /**
    * Lifts error value to the Scenario context.
    *
    * Error can be safely brought back to the return value domain using `attempt` method.
    * It also can be handled using various methods from `MonadError`
    * such as `handleErrorWith`, `recover` etc.
    *
    * @return Scenario which fails with `e`
    */
  def raiseError[F[_]](e: Throwable): Scenario[F, Nothing] =
    new Scenario[F, Nothing](Episode.RaiseError(e))

  implicit def monadErrorInstance[F[_]]: MonadError[Scenario[F, *], Throwable] =
    new MonadError[Scenario[F, *], Throwable] with StackSafeMonad[Scenario[F, *]] {
      def pure[A](a: A): Scenario[F, A] =
        Scenario.pure(a)

      def flatMap[A, B](scenario: Scenario[F, A])(fn: A => Scenario[F, B]): Scenario[F, B] =
        scenario.flatMap(fn)

      def raiseError[A](e: Throwable): Scenario[F, A] =
        Scenario.raiseError(e)

      def handleErrorWith[A](scenario: Scenario[F, A])(fn: Throwable => Scenario[F, A]): Scenario[F, A] =
        scenario.handleErrorWith(fn)
    }

  implicit def functionKInstance[F[_]]: F ~> Scenario[F, *] =
    FunctionK.lift(Scenario.eval)
}
