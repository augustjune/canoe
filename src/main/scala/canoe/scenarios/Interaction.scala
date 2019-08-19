package canoe.scenarios

import canoe.models.messages.TelegramMessage

object Interaction {

  def receive[F[_], A](pf: PartialFunction[TelegramMessage, A]): Interaction[F, A] =
    Scenario.start(pf.isDefinedAt).map(pf)

  def expect[F[_], A](pf: PartialFunction[TelegramMessage, A]): Interaction[F, A] =
    Scenario.next(pf.isDefinedAt).map(pf)

  def eval[F[_], A](fa: F[A]): Interaction[F, A] = Scenario.eval(fa)

}
