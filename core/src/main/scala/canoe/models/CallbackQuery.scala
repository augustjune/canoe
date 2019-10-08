package canoe.models

import canoe.models.messages.TelegramMessage

/**
  * Represents an incoming callback query from a callback button in an inline keyboard.
  *
  * If the button that originated the query was attached to a message sent by the bot,
  * the field message will be presented.
  * If the button was attached to a message sent via the bot (in inline mode),
  * the field inline_message_id will be presented.
  *
  * '''Note:'''
  * After the user presses an inline button, Telegram clients will display a progress bar until you call answerCallbackQuery.
  * It is, therefore, necessary to react by calling answerCallbackQuery even if no notification to the user
  * is needed (e.g., without specifying any of the optional parameters).
  *
  * @param id              Unique identifier for this query
  * @param from            Sender
  * @param message         Message with the callback button that originated the query.
  *                        Note that message content and message date will not be available if the message is too old
  * @param inlineMessageId Identifier of the message sent via the bot in inline mode, that originated the query
  * @param chatInstance    Identifier, uniquely corresponding to the chat to which the message with the callback button was sent.
  *                        Useful for high scores in games.
  * @param data            Data associated with the callback button. Be aware that a bad client can send arbitrary data in this field.
  * @param gameShortName   Short name of a Game to be returned, serves as the unique identifier for the game
  */
final case class CallbackQuery(id: String,
                               from: User,
                               message: Option[TelegramMessage],
                               inlineMessageId: Option[String],
                               chatInstance: String,
                               data: Option[String],
                               gameShortName: Option[String])
