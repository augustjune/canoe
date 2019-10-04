package canoe.models

/**
  * Represents a parameter of the inline keyboard button used to automatically authorize a user
  *
  * @param url                An HTTP URL to be opened with user authorization data added to the query string
  *                           when the button is pressed.
  *                           If the user refuses to provide authorization data,
  *                           the original URL without information about the user will be opened.
  *                           The data added is the same as described in Receiving authorization data.
  * @param forwardText        New text of the button in forwarded messages.
  * @param botUsername        Username of a bot, which will be used for user authorization.
  *                           If not specified, the current bot's username will be assumed.
  * @param requestWriteAccess Pass True to request the permission for your bot to send messages to the user.
  */
final case class LoginUrl(url: String,
                          forwardText: Option[String] = None,
                          botUsername: Option[String] = None,
                          requestWriteAccess: Option[Boolean] = None)
