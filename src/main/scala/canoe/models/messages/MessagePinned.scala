package canoe.models.messages

import canoe.models.Chat

case class MessagePinned(messageId: Int, chat: Chat, date: Int, message: TelegramMessage) extends TelegramMessage
