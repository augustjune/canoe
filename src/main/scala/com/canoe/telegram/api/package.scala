package com.canoe.telegram

import com.canoe.telegram.api.models.{ChatApi, InlineQueryApi, MessageApi}
import com.canoe.telegram.clients.RequestHandler
import com.canoe.telegram.models.{Chat, InlineQuery, Message}

package object api {
  implicit def chatApi[F[_] : RequestHandler](chat: Chat): ChatApi[F] =
    new ChatApi[F](chat)

  implicit def messageApi[F[_] : RequestHandler](message: Message): MessageApi[F] =
    new MessageApi[F](message)

  implicit def inlineQueryApi[F[_] : RequestHandler](query: InlineQuery): InlineQueryApi[F] =
    new InlineQueryApi[F](query)
}
