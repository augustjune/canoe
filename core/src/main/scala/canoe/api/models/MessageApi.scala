package canoe.api.models

import canoe.api._
import canoe.methods.messages._
import canoe.models.messages.TelegramMessage
import canoe.models.outgoing._
import canoe.models.{Chat, InlineKeyboardMarkup, ReplyMarkup}
import canoe.syntax.methodOps

/**
  * Telegram API for the any message object.
  * Offers a convenient access to the related Telegram methods in OO style.
  *
  * It is a conscious decision to provide this API via extension methods.
  */
final class MessageApi(private val message: TelegramMessage) extends AnyVal {

  private def chatId: Long = message.chat.id
  private def messageId: Int = message.messageId

  /**
    * Deletes this message.
    *
    * There are limitations what message can be deleted:
    * - A message can only be deleted if it was sent less than 48 hours ago.
    * - Bots can delete outgoing messages in private chats, groups, and supergroups.
    * - Bots can delete incoming messages in private chats.
    * - Bots granted can_post_messages permissions can delete outgoing messages in channels.
    * - If the bot is an administrator of a group, it can delete any message there.
    * - If the bot has can_delete_messages permission in a supergroup or a channel, it can delete any message there.
    */
  def delete[F[_]: TelegramClient]: F[Boolean] = DeleteMessage(chatId, messageId).call

  /**
    * Forwards this message to another chat.
    */
  def forward[F[_]: TelegramClient](to: Chat, disableNotification: Option[Boolean] = None): F[TelegramMessage] =
    ForwardMessage(to.id, chatId, messageId, disableNotification).call

  /**
    * Sends new message as a reply to this message.
    */
  def reply[F[_]: TelegramClient, M](content: MessageContent[M],
                                     replyMarkup: Option[ReplyMarkup] = None,
                                     disableNotification: Boolean = false): F[M] =
    message.chat.send(content, Some(messageId), replyMarkup, disableNotification)

  /**
    * Changes the text of this message.
    *
    * @return On success, if edited message is sent by the bot,
    *         the edited Message is returned, otherwise True is returned.
    */
  def editText[F[_]: TelegramClient](text: String): F[Either[Boolean, TelegramMessage]] =
    EditMessageText.direct(chatId, messageId, text = text).call

  /**
    * Changes the reply markup of this message.
    *
    * @return On success, if edited message is sent by the bot,
    *         the edited Message is returned, otherwise True is returned.
    */
  def editReplyMarkup[F[_]: TelegramClient](
    keyboard: Option[InlineKeyboardMarkup]
  ): F[Either[Boolean, TelegramMessage]] =
    EditMessageReplyMarkup.direct(chatId, messageId, replyMarkup = keyboard).call

  /**
    * Changes the caption of this message.
    *
    * @return On success, if edited message is sent by the bot,
    *         the edited Message is returned, otherwise True is returned.
    */
  def editCaption[F[_]: TelegramClient](caption: String): F[Either[Boolean, TelegramMessage]] =
    EditMessageCaption.direct(chatId, messageId, caption = Some(caption)).call
}
