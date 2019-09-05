package canoe.api.models

import canoe.api._
import canoe.methods.messages._
import canoe.models.messages.TelegramMessage
import canoe.models.outgoing._
import canoe.models.{Chat, InlineKeyboardMarkup, Poll, ReplyMarkup}

final class MessageApi[F[_]](message: TelegramMessage)(implicit client: TelegramClient[F]) {

  private def chatId: Long = message.chat.id
  private def messageId: Int = message.messageId

  def delete: F[Boolean] =
    client.execute(DeleteMessage(chatId, messageId))

  def forward(to: Chat, disableNotification: Option[Boolean] = None): F[TelegramMessage] =
    client.execute(ForwardMessage(to.id, chatId, disableNotification, messageId))

  def reply(content: MessageContent,
            replyMarkup: Option[ReplyMarkup] = None,
            disableNotification: Option[Boolean] = None): F[TelegramMessage] =
    message.chat.send(content, Some(messageId), replyMarkup, disableNotification)

  def editText(text: String): F[Either[Boolean, TelegramMessage]] =
    client.execute(EditMessageText(Some(chatId), Some(messageId), text = text))

  def editReplyMarkup(keyboard: Option[InlineKeyboardMarkup]): F[Either[Boolean, TelegramMessage]] =
    client.execute(EditMessageReplyMarkup(Some(chatId), Some(messageId), replyMarkup = keyboard))

  def editCaption(caption: Option[String]): F[Either[Boolean, TelegramMessage]] =
    client.execute(EditMessageCaption(Some(chatId), Some(messageId), caption = caption))

  // ToDo - handle case, when the message doesn't correspond to the poll
  def stopPoll(markup: Option[ReplyMarkup] = None): F[Poll] =
    client.execute(StopPoll(chatId, messageId, markup))
}
