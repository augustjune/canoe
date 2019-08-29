package canoe.models

import canoe.models.messages.TelegramMessage
import cats.syntax.functor._
import io.circe.Decoder
import io.circe.generic.auto._
import io.circe.generic.semiauto.deriveDecoder

sealed trait Update {
  def updateId: Long
}

object Update {

  implicit val updateDecoder: Decoder[Update] =
    List[Decoder[Update]](
      deriveDecoder[MessageReceived].widen,
      deriveDecoder[MessageEdited].widen,
      deriveDecoder[ChannelPost].widen,
      deriveDecoder[ChannelPostEdited].widen,
      deriveDecoder[PollUpdated].widen,
      deriveDecoder[InlineQueryReceived].widen,
      deriveDecoder[InlineResultSelected].widen,
      deriveDecoder[CallbackButtonSelected].widen,
      deriveDecoder[ShippingQueryReceived].widen,
      deriveDecoder[PreCheckoutQueryReceived].widen
    ).reduceLeft(_ or _)
}

final case class MessageReceived(updateId: Long, message: TelegramMessage) extends Update

final case class MessageEdited(updateId: Long, editedMessage: TelegramMessage) extends Update

final case class ChannelPost(updateId: Long, channelPost: TelegramMessage) extends Update

final case class ChannelPostEdited(updateId: Long, editedChannelPost: TelegramMessage) extends Update

final case class InlineQueryReceived(updateId: Long, inlineQuery: InlineQuery) extends Update

final case class InlineResultSelected(updateId: Long, chosenInlineResult: ChosenInlineResult) extends Update

final case class CallbackButtonSelected(updateId: Long, callbackQuery: CallbackQuery) extends Update

final case class ShippingQueryReceived(updateId: Long, shippingQuery: ShippingQuery) extends Update

final case class PreCheckoutQueryReceived(updateId: Long, preCheckoutQuery: PreCheckoutQuery) extends Update

final case class PollUpdated(updateId: Long, poll: Poll) extends Update
