package com.canoe.telegram.models.messages

import com.canoe.telegram.models.{Chat, User}

case class ChatMemberAdded(messageId: Int, chat: Chat, date: Int,
                           newChatMembers: Seq[User]) extends TelegramMessage
