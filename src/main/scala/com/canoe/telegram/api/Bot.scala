package com.canoe.telegram.api

import cats.Functor
import cats.effect.{Concurrent, Timer}
import cats.implicits._
import com.canoe.telegram.clients.RequestHandler
import com.canoe.telegram.models._
import fs2.Stream
import fs2.concurrent.Topic

class Bot[F[_] : Functor](topic: UpdateTopic[F]) {

  /**
    * Starts accepting the updates from the service
    */
  def start: Stream[F, Unit] = topic.start

  def messages: Stream[F, Message] = updates.collect {
    case u: ReceivedMessage => u.message
  }

  def inlineQueries: Stream[F, InlineQuery] = updates.collect {
    case u: ReceivedInlineQuery => u.inlineQuery
  }

  def updates: Stream[F, Update] = topic.fork
}

object Bot {
  def polling[F[_]](implicit client: RequestHandler[F],
                    C: Concurrent[F],
                    T: Timer[F]): F[Bot[F]] =
    for (topic <- Topic(Update.empty)) yield new Bot(new UpdatePolling(topic))
}
