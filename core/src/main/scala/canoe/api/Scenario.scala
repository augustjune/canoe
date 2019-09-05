package canoe.api

import canoe.models.messages.TelegramMessage
import canoe.scenarios.Episode
import cats.Applicative

object Scenario {

  def start[F[_], A](pf: PartialFunction[TelegramMessage, A]): Scenario[F, A] =
    Episode.first(pf.isDefinedAt).map(pf)

  def next[F[_], A](pf: PartialFunction[TelegramMessage, A]): Scenario[F, A] =
    Episode.next(pf.isDefinedAt).map(pf)

  def eval[F[_], A](fa: F[A]): Scenario[F, A] = Episode.eval(fa)

  def done[F[_]: Applicative]: Scenario[F, Unit] = eval(Applicative[F].unit)
}
