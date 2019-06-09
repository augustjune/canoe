package com.canoe.telegram.api.models

import com.canoe.telegram.clients.RequestHandler
import com.canoe.telegram.methods.messages._
import com.canoe.telegram.models.{Chat, Message}

final class MessageApi[F[_]](message: Message)
                            (implicit client: RequestHandler[F]) {

  def delete: F[Boolean] =
    client.execute(DeleteMessage(message.chat.id, message.messageId))

  def forward(to: Chat, disableNotification: Option[Boolean] = None): F[Message] =
    client.execute(ForwardMessage(to.id, message.chat.id, disableNotification, message.messageId))

  def reply(text: String): F[Message] =
    client.execute(SendMessage(message.chat.id, text, replyToMessageId = Option(message.messageId)))
}
