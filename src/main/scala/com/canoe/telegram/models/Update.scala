package com.canoe.telegram.models

sealed trait Update {
  def updateId: Long
}

object Update {
  def empty: Update = new Update { def updateId: Long = -1 }
}

final case class ReceivedMessage(updateId: Long, message: Message) extends Update

final case class EditedMessage(updateId: Long, editedMessage: Message) extends Update

final case class ChannelPost(updateId: Long, channelPost: Message) extends Update

final case class EditedChannelPost(updateId: Long, editedChannelPost: Message) extends Update

final case class PollUpdate(updateId: Long, poll: Poll) extends Update

final case class ReceivedInlineQuery(updateId: Long, inlineQuery: InlineQuery) extends Update

final case class ReceivedChosenInlineResult(updateId: Long, chosenInlineResult: ChosenInlineResult) extends Update

final case class ReceivedCallbackQuery(updateId: Long, callbackQuery: CallbackQuery) extends Update

final case class ReceivedShippingQuery(updateId: Long, shippingQuery: ShippingQuery) extends Update

final case class ReceivedPreCheckoutQuery(updateId: Long, preCheckoutQuery: PreCheckoutQuery) extends Update

