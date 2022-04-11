package canoe.models

import canoe.models.MemberStatus.MemberStatus
import io.circe.Decoder
import io.circe.generic.auto._
import io.circe.generic.semiauto

sealed trait ChatMember {
  def user: User
}

object ChatMember {

  implicit val chatMemberDecoder: Decoder[ChatMember] = Decoder.instance[ChatMember] { cursor =>
    cursor
      .get[MemberStatus]("status")
      .flatMap {
        case MemberStatus.Creator       => semiauto.deriveDecoder[ChatCreator].tryDecode(cursor)
        case MemberStatus.Administrator => semiauto.deriveDecoder[ChatAdministrator].tryDecode(cursor)
        case MemberStatus.Member        => semiauto.deriveDecoder[OrdinaryMember].tryDecode(cursor)
        case MemberStatus.Restricted    => semiauto.deriveDecoder[RestrictedMember].tryDecode(cursor)
        case MemberStatus.Left          => semiauto.deriveDecoder[LeftMember].tryDecode(cursor)
        case MemberStatus.Kicked        => semiauto.deriveDecoder[KickedMember].tryDecode(cursor)
      }
  }
}

final case class ChatCreator(user: User) extends ChatMember

final case class OrdinaryMember(user: User) extends ChatMember

final case class LeftMember(user: User) extends ChatMember

final case class KickedMember(user: User, untilDate: Option[Int]) extends ChatMember

final case class ChatAdministrator(user: User,
                                   customTitle: Option[String],
                                   canBeEdited: Option[Boolean],
                                   canChangeInfo: Option[Boolean],
                                   canPostMessages: Option[Boolean],
                                   canEditMessages: Option[Boolean],
                                   canDeleteMessages: Option[Boolean],
                                   canRestrictMembers: Option[Boolean],
                                   canPromoteMembers: Option[Boolean],
                                   canInviteUsers: Option[Boolean],
                                   canPinMessages: Option[Boolean])
    extends ChatMember

final case class RestrictedMember(user: User,
                                  untilDate: Option[Int],
                                  isMember: Option[Boolean],
                                  canChangeInfo: Option[Boolean],
                                  canInviteUsers: Option[Boolean],
                                  canPinMessages: Option[Boolean],
                                  canSendMessages: Option[Boolean],
                                  canSendMediaMessages: Option[Boolean],
                                  canSendOtherMessages: Option[Boolean],
                                  canAddWebPagePreviews: Option[Boolean])
    extends ChatMember
