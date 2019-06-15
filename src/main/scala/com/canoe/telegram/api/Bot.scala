package com.canoe.telegram.api

import cats.Functor
import cats.implicits._
import com.canoe.telegram.clients.RequestHandler
import com.canoe.telegram.methods.updates.GetUpdates
import com.canoe.telegram.models.{Message, Update}
import fs2.Stream

class Bot[F[_] : Functor](implicit client: RequestHandler[F]) {
  def messages: Stream[F, Message] =
    updates.map(_.message).collect {
      case Some(message) => message
    }

  def updates: Stream[F, Update] = pollUpdates(0)

  private def pollUpdates(startOffset: Long): Stream[F, Update] =
    Stream(()).repeat.covary[F]
      .evalMapAccumulate(startOffset) { case (offset, _) => requestUpdates(offset) }
      .flatMap { case (_, updates) => Stream.emits(updates) }

  private def requestUpdates(offset: Long): F[(Long, List[Update])] =
    client
      .execute(GetUpdates(Some(offset)))
      .map(_.toList)
      .map(updates => (lastOffset(updates).getOrElse(offset) + 1,  updates))


  private def lastOffset(updates: List[Update]): Option[Long] =
    updates match {
      case Nil => None
      case nonEmpty => Some(nonEmpty.map(_.updateId).max)
    }
}


