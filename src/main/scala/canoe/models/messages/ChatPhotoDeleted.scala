package canoe.models.messages

import canoe.models.Chat

case class ChatPhotoDeleted(messageId: Int, chat: Chat, date: Int, deleteChatPhoto: Boolean) extends TelegramMessage
