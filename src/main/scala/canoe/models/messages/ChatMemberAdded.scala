package canoe.models.messages

import canoe.models.{Chat, User}

case class ChatMemberAdded(messageId: Int, chat: Chat, date: Int,
                           newChatMembers: Seq[User]) extends TelegramMessage
