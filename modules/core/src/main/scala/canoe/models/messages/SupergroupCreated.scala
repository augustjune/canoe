package canoe.models.messages

import canoe.models.Chat

case class SupergroupCreated(messageId: Int, chat: Chat, date: Int, supergroupChatCreated: Boolean)
    extends TelegramMessage
