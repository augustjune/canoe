package canoe.models.messages

import canoe.models.Chat

case class ChatTitleChanged(messageId: Int, chat: Chat, date: Int, newChatTitle: String) extends TelegramMessage
