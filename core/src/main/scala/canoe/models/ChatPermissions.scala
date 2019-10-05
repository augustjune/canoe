package canoe.models

// ToDo - describe what None value means
/**
  * Describes actions that a non-administrator user is allowed to take in a chat.
  */
final case class ChatPermissions(canSendMessages: Option[Boolean] = None,
                                 canSendMediaMessages: Option[Boolean] = None,
                                 canSendPolls: Option[Boolean] = None,
                                 canSendOtherMessages: Option[Boolean] = None,
                                 canAddWebPagePreviews: Option[Boolean] = None,
                                 canChangeInfo: Option[Boolean] = None,
                                 canInviteUsers: Option[Boolean] = None,
                                 canPinMessages: Option[Boolean] = None)

object ChatPermissions {

  def canSendMessages: ChatPermissions = ChatPermissions(canSendMessages = Some(true))

  def canSendMediaMessages: ChatPermissions = ChatPermissions(canSendMediaMessages = Some(true))

  def canSendPolls: ChatPermissions = ChatPermissions(canSendPolls = Some(true))

  def canSendOtherMessages: ChatPermissions = ChatPermissions(canSendOtherMessages = Some(true))

  def canAddWebPagePreviews: ChatPermissions = ChatPermissions(canAddWebPagePreviews = Some(true))

  def canChangeInfo: ChatPermissions = ChatPermissions(canChangeInfo = Some(true))

  def canInviteUsers: ChatPermissions = ChatPermissions(canInviteUsers = Some(true))

  def canPinMessages: ChatPermissions = ChatPermissions(canPinMessages = Some(true))
}
