package canoe.models

import canoe.marshalling.codecs._
import canoe.models.messages.TelegramMessage
import cats.syntax.functor._
import io.circe.Decoder
import io.circe.generic.auto._
import io.circe.generic.semiauto

sealed trait Update {
  def updateId: Long
}

object Update {

  final case class Unknown(updateId: Long) extends Update

  implicit val updateDecoder: Decoder[Update] =
    List[Decoder[Update]](
      semiauto.deriveDecoder[MessageReceived].widen,
      semiauto.deriveDecoder[MessageEdited].widen,
      semiauto.deriveDecoder[ChannelPost].widen,
      semiauto.deriveDecoder[ChannelPostEdited].widen,
      semiauto.deriveDecoder[PollUpdated].widen,
      semiauto.deriveDecoder[InlineQueryReceived].widen,
      semiauto.deriveDecoder[InlineResultSelected].widen,
      semiauto.deriveDecoder[CallbackButtonSelected].widen,
      semiauto.deriveDecoder[ShippingQueryReceived].widen,
      semiauto.deriveDecoder[PreCheckoutQueryReceived].widen,
      semiauto.deriveDecoder[PollAnswerReceived].widen,
      semiauto.deriveDecoder[ChatMemberUpdated].widen,
      semiauto.deriveDecoder[Unknown].widen
    ).reduceLeft(_.or(_)).camelCase
}

/** New incoming message of any kind — text, photo, sticker, etc. */
final case class MessageReceived(updateId: Long, message: TelegramMessage) extends Update

/** New version of a message that is known to the bot and was edited. */
final case class MessageEdited(updateId: Long, editedMessage: TelegramMessage) extends Update

/** New incoming channel post of any kind — text, photo, sticker, etc. */
final case class ChannelPost(updateId: Long, channelPost: TelegramMessage) extends Update

/** New version of a channel post that is known to the bot and was edited. */
final case class ChannelPostEdited(updateId: Long, editedChannelPost: TelegramMessage) extends Update

/** New incoming inline query. */
final case class InlineQueryReceived(updateId: Long, inlineQuery: InlineQuery) extends Update

/** The result of an inline query that was chosen by a user and sent to their chat partner. */
final case class InlineResultSelected(updateId: Long, chosenInlineResult: ChosenInlineResult) extends Update

/** New incoming callback query. */
final case class CallbackButtonSelected(updateId: Long, callbackQuery: CallbackQuery) extends Update

/** New incoming shipping query. Only for invoices with flexible price. */
final case class ShippingQueryReceived(updateId: Long, shippingQuery: ShippingQuery) extends Update

/** New incoming pre-checkout query. Contains full information about checkout. */
final case class PreCheckoutQueryReceived(updateId: Long, preCheckoutQuery: PreCheckoutQuery) extends Update

/** A user changed their answer in a non-anonymous poll. Bots receive new votes only in polls that were sent by the bot itself. */
final case class PollAnswerReceived(updateId: Long, pollAnswer: PollAnswer) extends Update

/** New poll state. Bots receive only updates about stopped polls and polls, which are sent by the bot. */
final case class PollUpdated(updateId: Long, poll: Poll) extends Update

/** A user changed state in the chat: blocked the bot or left. */
final case class ChatMemberUpdated(updateId: Long, chat: Chat, from: User, date: Int, oldChatMember: ChatMember, newChatMember: ChatMember) extends Update

