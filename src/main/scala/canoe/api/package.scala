package canoe

import canoe.api.models.{ChatApi, InlineQueryApi, MessageApi}
import canoe.clients.RequestHandler
import canoe.models.messages.TelegramMessage
import canoe.models.{Chat, InlineQuery}

package object api {
  implicit def chatApi[F[_] : RequestHandler](chat: Chat): ChatApi[F] =
    new ChatApi[F](chat)

  implicit def messageApi[F[_] : RequestHandler](message: TelegramMessage): MessageApi[F] =
    new MessageApi[F](message)

  implicit def inlineQueryApi[F[_] : RequestHandler](query: InlineQuery): InlineQueryApi[F] =
    new InlineQueryApi[F](query)
}
