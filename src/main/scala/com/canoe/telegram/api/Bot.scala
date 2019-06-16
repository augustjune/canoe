package com.canoe.telegram.api

import cats.Functor
import cats.implicits._
import com.canoe.telegram.models.{InlineQuery, Message, Update}
import fs2.Stream

class Bot[F[_] : Functor](updateTopic: UpdatePolling[F]) {
  def messages: Stream[F, Message] =
    updates.map(_.message).collect {
      case Some(v) => v
    }

  def inlineQueries: Stream[F, InlineQuery] =
    updates.map(_.inlineQuery).collect {
      case Some(v) => v
    }

  def updates: Stream[F, Update] = updateTopic.fork
}


