package canoe.models.messages

import canoe.models.{Chat, PhotoSize}

case class ChatPhotoChanged(messageId: Int, chat: Chat, date: Int, newChatPhoto: Seq[PhotoSize]) extends TelegramMessage
