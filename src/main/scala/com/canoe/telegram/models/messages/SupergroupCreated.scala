package com.canoe.telegram.models.messages

import com.canoe.telegram.models.Chat

case class SupergroupCreated(messageId: Int, chat: Chat, date: Int,
                             supergroupChatCreated: Boolean) extends TelegramMessage
