package canoe.models

import canoe.models.MemberStatus.MemberStatus
import io.circe.Decoder
import io.circe.generic.auto._
import io.circe.generic.semiauto.deriveDecoder

sealed trait ChatMember {
  def user: User
}

object ChatMember {

  implicit val chatMemberDecoder: Decoder[ChatMember] = Decoder.instance[ChatMember] { cursor =>
    cursor
      .get[MemberStatus]("status")
      .map {
        case MemberStatus.Creator       => deriveDecoder[ChatCreator]
        case MemberStatus.Administrator => deriveDecoder[ChatAdministrator]
        case MemberStatus.Member        => deriveDecoder[CurrentMember]
        case MemberStatus.Restricted    => deriveDecoder[RestrictedMember]
        case MemberStatus.Left          => deriveDecoder[LeftMember]
        case MemberStatus.Kicked        => deriveDecoder[KickedMember]
      }
      .flatMap(_.tryDecode(cursor))
  }
}

case class ChatCreator(user: User) extends ChatMember

case class ChatAdministrator(user: User,
                             canBeEdited: Boolean,
                             canChangeInfo: Boolean,
                             canPostMessages: Boolean,
                             canEditMessages: Boolean,
                             canDeleteMessages: Boolean,
                             canInviteUsers: Boolean,
                             canRestrictMembers: Boolean,
                             canPinMessages: Boolean,
                             canPromoteMembers: Boolean)
    extends ChatMember

case class CurrentMember(user: User) extends ChatMember

case class RestrictedMember(user: User,
                            untilDate: Int,
                            isMember: Boolean,
                            canSendMessages: Boolean,
                            canSendMediaMessages: Boolean,
                            canSendOtherMessages: Boolean,
                            canAddWebPagePreviews: Boolean)
    extends ChatMember

case class LeftMember(user: User) extends ChatMember

case class KickedMember(user: User, untilDate: Int) extends ChatMember
