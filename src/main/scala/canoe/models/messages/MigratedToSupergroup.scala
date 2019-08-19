package canoe.models.messages

import canoe.models.Chat

case class MigratedToSupergroup(messageId: Int, chat: Chat, date: Int,
                                migrateToChatId: Long) extends TelegramMessage
