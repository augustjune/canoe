package canoe.api.sources

import canoe.api.UpdateSource
import canoe.clients.TelegramClient
import canoe.methods.updates.GetUpdates
import canoe.models.Update
import cats.Functor
import cats.syntax.functor._
import fs2.Stream

private[api] class Polling[F[_]: Functor](client: TelegramClient[F]) extends UpdateSource[F] {

  def updates: Stream[F, Update] = pollUpdates(0)

  private def pollUpdates(startOffset: Long): Stream[F, Update] =
    Stream(()).repeat
      .covary[F]
      .evalMapAccumulate(startOffset) { case (offset, _) => requestUpdates(offset) }
      .flatMap { case (_, updates) => Stream.emits(updates) }

  private def requestUpdates(offset: Long): F[(Long, List[Update])] =
    client
      .execute(GetUpdates(Some(offset)))
      .map(updates => (lastId(updates).map(_ + 1).getOrElse(offset), updates))

  private def lastId(updates: List[Update]): Option[Long] =
    updates match {
      case Nil      => None
      case nonEmpty => Some(nonEmpty.map(_.updateId).max)
    }
}
