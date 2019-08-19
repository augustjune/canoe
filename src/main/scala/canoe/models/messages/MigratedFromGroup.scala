package canoe.models.messages

import canoe.models.Chat

case class MigratedFromGroup(messageId: Int, chat: Chat, date: Int,
                             migrateFromChatId: Long) extends TelegramMessage
