package canoe.models.messages

import canoe.models.{Chat, PhotoSize, SuccessfulPayment, User}
import cats.syntax.functor._
import io.circe.Decoder
import io.circe.generic.auto._
import io.circe.generic.semiauto.deriveDecoder

sealed trait SystemMessage extends TelegramMessage

object SystemMessage {

  implicit val systemMessageDecoder: Decoder[SystemMessage] =
    List[Decoder[SystemMessage]](
      deriveDecoder[ChatMemberAdded].widen,
      deriveDecoder[ChannelCreated].widen,
      deriveDecoder[ChatMemberLeft].widen,
      deriveDecoder[ChatPhotoChanged].widen,
      deriveDecoder[ChatPhotoDeleted].widen,
      deriveDecoder[ChatTitleChanged].widen,
      deriveDecoder[MigratedFromGroup].widen,
      deriveDecoder[MigratedToSupergroup].widen,
      deriveDecoder[SuccessfulPaymentMessage].widen,
      deriveDecoder[GroupChatCreated].widen,
      deriveDecoder[SupergroupCreated].widen,
      deriveDecoder[WebsiteConnected].widen
    ).reduceLeft(_.or(_))
}

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

final case class GroupChatCreated(messageId: Int, chat: Chat, date: Int, groupChatCreated: Boolean)
    extends SystemMessage

final case class SupergroupCreated(messageId: Int, chat: Chat, date: Int, supergroupChatCreated: Boolean)
    extends SystemMessage

final case class SuccessfulPaymentMessage(messageId: Int, chat: Chat, date: Int, successfulPayment: SuccessfulPayment)
    extends SystemMessage

final case class WebsiteConnected(messageId: Int, chat: Chat, date: Int, connectedWebsite: String) extends SystemMessage
