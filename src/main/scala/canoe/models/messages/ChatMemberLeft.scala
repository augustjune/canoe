package canoe.models.messages

import canoe.models.{Chat, User}

case class ChatMemberLeft(messageId: Int, chat: Chat, date: Int,
                          leftChatMember: User) extends TelegramMessage
