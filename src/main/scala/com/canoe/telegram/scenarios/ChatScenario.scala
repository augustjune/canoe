package com.canoe.telegram.scenarios

import com.canoe.telegram.models.messages.TelegramMessage

object ChatScenario {

  type ChatScenario[F[_], A] = Scenario[F, TelegramMessage, A]

  def start[F[_], A](pf: PartialFunction[TelegramMessage, A]): ChatScenario[F, A] =
    Scenario.start(pf.isDefinedAt).map(pf)

  def next[F[_], A](pf: PartialFunction[TelegramMessage, A]): ChatScenario[F, A] =
    Scenario.next(pf.isDefinedAt).map(pf)

  def eval[F[_], A](fa: F[A]): ChatScenario[F, A] = Scenario.eval(fa)

}
