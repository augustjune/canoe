package canoe.api.models

import canoe.api._
import canoe.methods.messages._
import canoe.models.messages.{PollMessage, TelegramMessage}
import canoe.models.outgoing._
import canoe.models.{Chat, InlineKeyboardMarkup, Poll, ReplyMarkup}
import canoe.syntax.methodOps
import cats.ApplicativeError

final class MessageApi[F[_]](message: TelegramMessage)(implicit client: TelegramClient[F]) {

  private def chatId: Long = message.chat.id
  private def messageId: Int = message.messageId

  /**
    * Deletes this message
    *
    * There are limitations what message can be deleted:
    * - A message can only be deleted if it was sent less than 48 hours ago.
    * - Bots can delete outgoing messages in private chats, groups, and supergroups.
    * - Bots can delete incoming messages in private chats.
    * - Bots granted can_post_messages permissions can delete outgoing messages in channels.
    * - If the bot is an administrator of a group, it can delete any message there.
    * - If the bot has can_delete_messages permission in a supergroup or a channel, it can delete any message there.
    */
  def delete: F[Boolean] = DeleteMessage(chatId, messageId).call

  /**
    * Forwards this message to another chat
    */
  def forward(to: Chat, disableNotification: Option[Boolean] = None): F[TelegramMessage] =
    ForwardMessage(to.id, chatId, disableNotification, messageId).call

  /**
    * Sends new message as a reply to this message
    */
  def reply(content: MessageContent,
            replyMarkup: Option[ReplyMarkup] = None,
            disableNotification: Option[Boolean] = None): F[TelegramMessage] =
    message.chat.send(content, Some(messageId), replyMarkup, disableNotification)

  /**
    * Changes the text of this message
    *
    * @return On success, if edited message is sent by the bot,
    *         the edited Message is returned, otherwise True is returned.
    */
  def editText(text: String): F[Either[Boolean, TelegramMessage]] =
    EditMessageText(Some(chatId), Some(messageId), text = text).call

  /**
    * Changes the reply markup of this message
    * @return On success, if edited message is sent by the bot,
    *         the edited Message is returned, otherwise True is returned.
    */
  def editReplyMarkup(keyboard: Option[InlineKeyboardMarkup]): F[Either[Boolean, TelegramMessage]] =
    EditMessageReplyMarkup(Some(chatId), Some(messageId), replyMarkup = keyboard).call

  /**
    * Changes the caption of this message
    *
    * @return On success, if edited message is sent by the bot,
    *         the edited Message is returned, otherwise True is returned.
    */
  def editCaption(caption: Option[String]): F[Either[Boolean, TelegramMessage]] =
    EditMessageCaption(Some(chatId), Some(messageId), caption = caption).call

  /**
    * Stops the poll, which is represented by this message.
    *
    * @return On success, the stopped Poll with the final results is returned.
    */
  def stopPoll(markup: Option[ReplyMarkup] = None)(implicit F: ApplicativeError[F, Throwable]): F[Poll] =
    message match {
      case _: PollMessage => StopPoll(chatId, messageId, markup).call
      case _              => F.raiseError(new RuntimeException("This message is not a poll"))
    }

}
