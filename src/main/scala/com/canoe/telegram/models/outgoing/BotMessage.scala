package com.canoe.telegram.models.outgoing

import com.canoe.telegram.models.ReplyMarkup

/**
  * Outgoing message, which is going to be sent by the bot
  */
case class BotMessage(content: MessageContent,
                      disableNotification: Option[Boolean] = None,
                      replyToMessageId: Option[Int] = None,
                      replyMarkup: Option[ReplyMarkup] = None) {

  def silentMode(value: Boolean): BotMessage =
    copy(disableNotification = Some(value))

  def withReplyMarkup(markup: ReplyMarkup): BotMessage =
    copy(replyMarkup = Some(markup))

  def asReplyToMessage(messageId: Int): BotMessage =
    copy(replyToMessageId = Some(messageId))
}
