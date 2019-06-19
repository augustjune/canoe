package com.canoe.telegram

import com.canoe.telegram.api.models.{ChatApi, InlineQueryApi, MessageApi}
import com.canoe.telegram.clients.RequestHandler
import com.canoe.telegram.models.messages.TelegramMessage
import com.canoe.telegram.models.{Chat, InlineQuery}

package object api {
  implicit def chatApi[F[_] : RequestHandler](chat: Chat): ChatApi[F] =
    new ChatApi[F](chat)

  implicit def messageApi[F[_] : RequestHandler](message: TelegramMessage): MessageApi[F] =
    new MessageApi[F](message)

  implicit def inlineQueryApi[F[_] : RequestHandler](query: InlineQuery): InlineQueryApi[F] =
    new InlineQueryApi[F](query)
}
