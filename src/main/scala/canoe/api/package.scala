package canoe

import canoe.api.models._
import canoe.clients.TelegramClient
import canoe.models.messages.TelegramMessage
import canoe.models.{Chat, InlineQuery}

package object api {
  implicit def chatApi[F[_] : TelegramClient](chat: Chat): ChatApi[F] =
    new ChatApi[F](chat)

  implicit def messageApi[F[_] : TelegramClient](message: TelegramMessage): MessageApi[F] =
    new MessageApi[F](message)

  implicit def inlineQueryApi[F[_] : TelegramClient](query: InlineQuery): InlineQueryApi[F] =
    new InlineQueryApi[F](query)
}
