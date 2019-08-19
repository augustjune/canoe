package canoe.models

import canoe.models.messages.TelegramMessage

sealed trait Update {
  def updateId: Long
}

final case class ReceivedMessage(updateId: Long, message: TelegramMessage) extends Update

final case class EditedMessage(updateId: Long, editedMessage: TelegramMessage) extends Update

final case class ChannelPost(updateId: Long, channelPost: TelegramMessage) extends Update

final case class EditedChannelPost(updateId: Long, editedChannelPost: TelegramMessage) extends Update

final case class PollUpdate(updateId: Long, poll: Poll) extends Update

final case class ReceivedInlineQuery(updateId: Long, inlineQuery: InlineQuery) extends Update

final case class ReceivedChosenInlineResult(updateId: Long, chosenInlineResult: ChosenInlineResult) extends Update

final case class ReceivedCallbackQuery(updateId: Long, callbackQuery: CallbackQuery) extends Update

final case class ReceivedShippingQuery(updateId: Long, shippingQuery: ShippingQuery) extends Update

final case class ReceivedPreCheckoutQuery(updateId: Long, preCheckoutQuery: PreCheckoutQuery) extends Update
