package canoe

import canoe.models.messages.TelegramMessage

package object scenarios {

  type Scenario[F[_], A] = Episode[F, TelegramMessage, A]

  object Scenario {

    def start[F[_], A](pf: PartialFunction[TelegramMessage, A]): Scenario[F, A] =
      Episode.first(pf.isDefinedAt).map(pf)

    def next[F[_], A](pf: PartialFunction[TelegramMessage, A]): Scenario[F, A] =
      Episode.next(pf.isDefinedAt).map(pf)

    def eval[F[_], A](fa: F[A]): Scenario[F, A] = Episode.eval(fa)

  }
}
