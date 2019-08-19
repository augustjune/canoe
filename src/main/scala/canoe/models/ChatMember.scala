package canoe.models

sealed trait ChatMember {
  def user: User
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
                             canPromoteMembers: Boolean
                            ) extends ChatMember

case class CurrentMember(user: User) extends ChatMember

case class RestrictedMember(user: User,
                            untilDate: Int,
                            isMember: Boolean,
                            canSendMessages: Boolean,
                            canSendMediaMessages: Boolean,
                            canSendOtherMessages: Boolean,
                            canAddWebPagePreviews: Boolean
                           ) extends ChatMember

case class LeftMember(user: User) extends ChatMember

case class KickedMember(user: User, untilDate: Int) extends ChatMember
