package canoe.models

import canoe.models.messages.TelegramMessage

sealed trait Update {
  def updateId: Long
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
