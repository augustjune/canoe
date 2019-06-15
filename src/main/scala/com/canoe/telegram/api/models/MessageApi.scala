package com.canoe.telegram.api.models

import com.canoe.telegram.clients.RequestHandler
import com.canoe.telegram.methods.chats.StopPoll
import com.canoe.telegram.methods.messages._
import com.canoe.telegram.models.{Chat, Message, Poll, ReplyMarkup}

final class MessageApi[F[_]](message: Message)
                            (implicit client: RequestHandler[F]) {

  def delete: F[Boolean] =
    client.execute(DeleteMessage(chatId, messageId))

  def forward(to: Chat, disableNotification: Option[Boolean] = None): F[Message] =
    client.execute(ForwardMessage(to.id, chatId, disableNotification, messageId))

  def reply(text: String): F[Message] =
    client.execute(SendMessage(chatId, text, replyToMessageId = Option(messageId)))

  // ToDo - handle case, when the message doesn't correspond to the poll
  def stopPoll(markup: Option[ReplyMarkup] = None): F[Poll] =
    client.execute(StopPoll(message.chat.id, message.messageId, markup))

  private def chatId: Long = message.chat.id
  private def messageId: Int = message.messageId
}
