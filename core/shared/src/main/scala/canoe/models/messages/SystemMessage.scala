package canoe.models.messages

import canoe.models.{Chat, DiceResult, PhotoSize, SuccessfulPayment, User}
import cats.syntax.functor._
import io.circe.Decoder
import io.circe.generic.auto._
import io.circe.generic.semiauto

sealed trait SystemMessage extends TelegramMessage with Product

object SystemMessage {

  implicit val systemMessageDecoder: Decoder[SystemMessage] =
    List[Decoder[SystemMessage]](
      semiauto.deriveDecoder[MessagePinned].widen,
      semiauto.deriveDecoder[ChatMemberAdded].widen,
      semiauto.deriveDecoder[ChannelCreated].widen,
      semiauto.deriveDecoder[ChatMemberLeft].widen,
      semiauto.deriveDecoder[ChatPhotoChanged].widen,
      semiauto.deriveDecoder[ChatPhotoDeleted].widen,
      semiauto.deriveDecoder[ChatTitleChanged].widen,
      semiauto.deriveDecoder[MigratedFromGroup].widen,
      semiauto.deriveDecoder[MigratedToSupergroup].widen,
      semiauto.deriveDecoder[SuccessfulPaymentMessage].widen,
      semiauto.deriveDecoder[GroupChatCreated].widen,
      semiauto.deriveDecoder[SupergroupCreated].widen,
      semiauto.deriveDecoder[WebsiteConnected].widen,
      semiauto.deriveDecoder[DiceThrownMessage].widen
    ).reduceLeft(_.or(_))
}

final case class MessagePinned(messageId: Int, chat: Chat, date: Int, pinnedMessage: TelegramMessage) extends SystemMessage

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

final case class DiceThrownMessage(messageId: Int, chat: Chat, date: Int, dice: DiceResult) extends SystemMessage
