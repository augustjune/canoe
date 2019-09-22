package canoe.api.sources

import canoe.api.{TelegramClient, UpdateSource}
import canoe.methods.updates.GetUpdates
import canoe.models.Update
import cats.Functor
import cats.effect.Timer
import cats.syntax.functor._
import fs2.Stream

import scala.concurrent.duration._

private[api] class Polling[F[_]: Functor](client: TelegramClient[F], timeout: FiniteDuration) {

  def pollUpdates(startOffset: Long): Stream[F, List[Update]] =
    Stream(()).repeat
      .covary[F]
      .evalMapAccumulate(startOffset) { case (offset, _) => requestUpdates(offset) }
      .map { case (_, updates) => updates }

  private def requestUpdates(offset: Long): F[(Long, List[Update])] =
    client
      .execute(GetUpdates(offset = Some(offset), timeout = Some(timeout.toSeconds.toInt)))
      .map(updates => (lastId(updates).map(_ + 1).getOrElse(offset), updates))

  private def lastId(updates: List[Update]): Option[Long] =
    updates match {
      case Nil      => None
      case nonEmpty => Some(nonEmpty.map(_.updateId).max)
    }
}

object Polling {

  /**
    * Default timeout duration for long polling
    */
  private val longPollTimeout: FiniteDuration = 30 seconds

  /**
    * Polls new batch of updates whenever consumer is ready
    */
  def continual[F[_]: TelegramClient: Functor]: UpdateSource[F] =
    new Polling[F](implicitly[TelegramClient[F]], longPollTimeout) with UpdateSource[F] {
      def updates: Stream[F, Update] = pollUpdates(0).flatMap(Stream.emits)
    }

  /**
    * Polls new batch of updates when consumer is ready and `interval` passed since the last polling
    */
  def metered[F[_]: TelegramClient: Functor: Timer](interval: FiniteDuration): UpdateSource[F] =
    new Polling[F](implicitly[TelegramClient[F]], longPollTimeout) with UpdateSource[F] {
      def updates: Stream[F, Update] =
        pollUpdates(0).metered(interval).flatMap(Stream.emits)
    }
}
