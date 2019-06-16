package com.canoe.telegram.api

import cats.effect.{Concurrent, Timer}
import cats.implicits._
import com.canoe.telegram.clients.RequestHandler
import com.canoe.telegram.methods.updates.GetUpdates
import com.canoe.telegram.models.Update
import fs2.Stream
import fs2.concurrent.Topic

import scala.concurrent.duration._

class UpdatePolling[F[_]](topic: Topic[F, Update])(implicit client: RequestHandler[F],
                                                   F: Concurrent[F],
                                                   T: Timer[F]) {

  /**
    * Creates a new stream of updates
    */
  def fork: Stream[F, Update] = topic.subscribe(10)

  def start: Stream[F, Unit] = pollUpdates(0).through(topic.publish)


  private def pollUpdates(startOffset: Long): Stream[F, Update] =
    Stream(()).repeat.covary[F].debounce(5.second)
      .evalMapAccumulate(startOffset) { case (offset, _) => requestUpdates(offset) }
      .flatMap { case (_, updates) => Stream.emits(updates) }

  private def requestUpdates(offset: Long): F[(Long, List[Update])] =
    client
      .execute(GetUpdates(Some(offset)))
      .map(_.toList)
      .map(updates => (lastOffset(updates).map(_ + 1).getOrElse(offset), updates))


  private def lastOffset(updates: List[Update]): Option[Long] =
    updates match {
      case Nil => None
      case nonEmpty => Some(nonEmpty.map(_.updateId).max)
    }
}

object UpdatePolling {
  def apply[F[_]]()(implicit client: RequestHandler[F], F: Concurrent[F], T: Timer[F]): F[UpdatePolling[F]] =
    for (topic <- Topic(Update(0))) yield new UpdatePolling[F](topic)
}
