package canoe.models

/** This object represents one button of an inline keyboard.
  * You must use exactly one of the optional fields.
  *
  * ''Notes:''
  *   This offers an easy way for users to start using your bot in inline mode when they are currently in a private chat with it.
  *   Especially useful when combined with switch_pm... actions - in this case the user will be automatically returned to the
  *   chat they switched from, skipping the chat selection screen.
  *   This will only work in Telegram versions released after 9 April, 2016. Older clients will display unsupported message.
  *
  * @param text               String Label text on the button
  * @param url                String Optional HTTP url to be opened when button is pressed
  * @param callbackData       String Optional Data to be sent in a callback query to the bot when button is pressed, 1-64 bytes
  * @param switchInlineQuery  String Optional If set, pressing the button will prompt the user to select one of their chats,
  *                           open that chat and insert the bot's username and the specified inline query in the input field.
  *                           Can be empty, in which case just the bot's username will be inserted.
  * @param switchInlineQueryCurrentChat String Optional. If set, pressing the button will insert the bot's username and the
  *                                     specified inline query in the current chat's input field. Can be empty,
  *                                     in which case only the bot's username will be inserted.
  *                                     This offers a quick way for the user to open your bot in inline mode in the same chat -
  *                                     good for selecting something from multiple options.
  * @param callbackGame       CallbackGame Optional. Description of the game that will be launched when the user presses the button.
  *
  * NOTE: This type of button must always be the first button in the first row.
  */
case class InlineKeyboardButton private (text: String,
                                         callbackData: Option[String] = None,
                                         url: Option[String] = None,
                                         loginUrl: Option[LoginUrl] = None,
                                         switchInlineQuery: Option[String] = None,
                                         switchInlineQueryCurrentChat: Option[String] = None,
                                         callbackGame: Option[CallbackGame] = None,
                                         pay: Option[Boolean] = None) {
  require(
    Seq[Option[_]](
      callbackData,
      url,
      switchInlineQuery,
      switchInlineQueryCurrentChat,
      callbackGame,
      pay
    ).count(_.isDefined) == 1,
    "Exactly one of the optional fields should be set."
  )
}

object InlineKeyboardButton {

  /**
    * Interactive button that will send a callback.
    * @param cbd Data to be sent in a callback query to the bot when button is pressed, 1-64 bytes
    */
  def callbackData(text: String, cbd: String): InlineKeyboardButton =
    InlineKeyboardButton(text, callbackData = Some(cbd))

  /**
    * Button that opens an URL.
    * @param url HTTP url to be opened when button is pressed
    */
  def url(text: String, url: String): InlineKeyboardButton =
    InlineKeyboardButton(text, url = Some(url))

  /**
    * '''Note:'''
    *   This type of button must always be the first button in the first row.
    */
  def callbackGame(text: String): InlineKeyboardButton =
    InlineKeyboardButton(text, callbackGame = Some(CallbackGame))

  /**
    * Pressing the button will prompt the user to select one of their chats,
    * open that chat and insert the bot's username and the specified inline query in the input field.
    * Can be empty, in which case just the bot's username will be inserted.
    */
  def switchInlineQuery(text: String, siq: String): InlineKeyboardButton =
    InlineKeyboardButton(text, switchInlineQuery = Some(siq))

  /**
    * Pressing the button will insert the bot's username and the
    * specified inline query in the current chat's input field.
    * Can be empty, in which case only the bot's username will be inserted.
    * This offers a quick way for the user to open your bot in inline mode in the same chat -
    * good for selecting something from multiple options.
    */
  def switchInlineQueryCurrentChat(text: String, siqcc: String): InlineKeyboardButton =
    InlineKeyboardButton(text, switchInlineQueryCurrentChat = Some(siqcc))

  /**
    * Pay button.
    * '''Note:'''
    *   This type of button must always be the first button in the first row.
    */
  def pay(text: String): InlineKeyboardButton =
    InlineKeyboardButton(text, pay = Some(true))
}
