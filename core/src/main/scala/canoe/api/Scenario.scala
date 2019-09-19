package canoe.api

import canoe.models.messages.TelegramMessage
import canoe.scenarios.Episode

object Scenario {

  /**
    * Defines the beginning of the scenario.
    *
    * Each input for which `pf` is defined is going to be matched
    * and transformed into `A` type
    */
  def start[F[_], A](pf: PartialFunction[TelegramMessage, A]): Scenario[F, A] =
    Episode.first(pf.isDefinedAt).map(pf)

  /**
    * Defines following step of the scenario.
    *
    * If `pf` is defined for the first input element,
    * it is going to be matched and transformed into `A` type
    */
  def next[F[_], A](pf: PartialFunction[TelegramMessage, A]): Scenario[F, A] =
    Episode.next(pf.isDefinedAt).map(pf)

  /**
    * Suspends an effectful value of type `A` into Scenario context
    *
    * Generally used for describing bot part of a scenario
    * (e.g. sending messages, making calls to external APIs, etc.)
    */
  def eval[F[_], A](fa: F[A]): Scenario[F, A] = Episode.eval(fa)

  /**
    * Lifts pure value to Scenario context
    */
  def pure[F[_], A](a: A): Scenario[F, A] = Episode.pure(a)

  def done[F[_]]: Scenario[F, Unit] = pure(())
}
