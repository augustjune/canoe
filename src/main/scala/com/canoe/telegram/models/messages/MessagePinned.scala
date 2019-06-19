package com.canoe.telegram.models.messages

import com.canoe.telegram.models.Chat

case class MessagePinned(messageId: Int, chat: Chat, date: Int,
                         message: TelegramMessage) extends TelegramMessage
