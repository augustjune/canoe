package canoe.models

/** Telegram user or bot.
  *
  * @param id                      Unique identifier
  * @param isBot                   True, if this user is a bot
  * @param firstName               User's or bot's first name
  * @param lastName                User's or bot's last name
  * @param username                User's or bot's username
  * @param languageCode            IETF language tag of the user's language
  * @param canJoinGroups           True, if the bot can be invited to groups. Returned only in getMe.
  * @param canReadAllGroupMessages True, if privacy mode is disabled for the bot. Returned only in getMe.
  * @param supportsInlineQueries   True, if the bot supports inline queries. Returned only in getMe.
  */
final case class User(id: Int,
                      isBot: Boolean,
                      firstName: String,
                      lastName: Option[String],
                      username: Option[String],
                      languageCode: Option[String],
                      canJoinGroups: Option[Boolean],
                      canReadAllGroupMessages: Option[Boolean],
                      supportsInlineQueries: Option[Boolean]
)