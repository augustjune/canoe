package com.canoe.telegram.api

import cats.effect.Concurrent
import cats.implicits._
import com.canoe.telegram.clients.RequestHandler
import com.canoe.telegram.methods.updates.GetUpdates
import com.canoe.telegram.models.Update
import fs2.Stream
import fs2.concurrent.Topic

class UpdatePolling[F[_]](topic: Topic[F, Update])(implicit client: RequestHandler[F],
                                                   F: Concurrent[F]) extends UpdateTopic[F] {

  def fork: Stream[F, Update] = topic.subscribe(10) // half-random 10

  def start: Stream[F, Unit] = pollUpdates(0).through(topic.publish)

  private def pollUpdates(startOffset: Long): Stream[F, Update] =
    Stream(()).repeat.covary[F]
      .evalMapAccumulate(startOffset) { case (offset, _) => requestUpdates(offset) }
      .flatMap { case (_, updates) => Stream.emits(updates) }

  private def requestUpdates(offset: Long): F[(Long, List[Update])] =
    client
      .execute(GetUpdates(Some(offset)))
      .map(_.toList)
      .map(updates => (lastId(updates).map(_ + 1).getOrElse(offset), updates))


  private def lastId(updates: List[Update]): Option[Long] =
    updates match {
      case Nil => None
      case nonEmpty => Some(nonEmpty.map(_.updateId).max)
    }
}
