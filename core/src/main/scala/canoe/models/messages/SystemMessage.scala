package canoe.models.messages

import canoe.models.{Chat, PhotoSize, SuccessfulPayment, User}

sealed trait SystemMessage extends TelegramMessage

final case class MessagePinned(messageId: Int, chat: Chat, date: Int, pinnedMessage: UserMessage) extends SystemMessage

final case class ChannelCreated(messageId: Int, chat: Chat, date: Int, channelChatCreated: Boolean)
    extends SystemMessage

final case class ChatMemberAdded(messageId: Int, chat: Chat, date: Int, newChatMembers: Seq[User]) extends SystemMessage

final case class ChatMemberLeft(messageId: Int, chat: Chat, date: Int, leftChatMember: User) extends SystemMessage

final case class ChatPhotoChanged(messageId: Int, chat: Chat, date: Int, newChatPhoto: Seq[PhotoSize])
    extends SystemMessage

final case class ChatPhotoDeleted(messageId: Int, chat: Chat, date: Int, deleteChatPhoto: Boolean) extends SystemMessage

final case class ChatTitleChanged(messageId: Int, chat: Chat, date: Int, newChatTitle: String) extends SystemMessage

final case class MigratedFromGroup(messageId: Int, chat: Chat, date: Int, migrateFromChatId: Long) extends SystemMessage

final case class MigratedToSupergroup(messageId: Int, chat: Chat, date: Int, migrateToChatId: Long)
    extends SystemMessage

final case class SupergroupCreated(messageId: Int, chat: Chat, date: Int, supergroupChatCreated: Boolean)
    extends SystemMessage

final case class SuccessfulPaymentMessage(messageId: Int, chat: Chat, date: Int, successfulPayment: SuccessfulPayment)
    extends SystemMessage

final case class WebsiteConnected(messageId: Int, chat: Chat, date: Int, connectedWebsite: String) extends SystemMessage
