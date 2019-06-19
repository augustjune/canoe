package com.canoe.telegram.models.messages

import com.canoe.telegram.models.Chat

case class MigratedToSupergroup(messageId: Int, chat: Chat, date: Int,
                                migrateToChatId: Long) extends TelegramMessage
