package canoe.models

import io.circe.Encoder
import io.circe.generic.auto._
import io.circe.generic.semiauto._

/**
  * Button of an inline keyboard.
  * You must use exactly one of the optional fields.
  *
  * ''Notes:''
  * This offers an easy way for users to start using your bot in inline mode when they are currently in a private chat with it.
  * Especially useful when combined with switch_pm... actions - in this case the user will be automatically returned to the
  * chat they switched from, skipping the chat selection screen.
  * This will only work in Telegram versions released after 9 April, 2016. Older clients will display unsupported message.
  *
  * NOTE: This type of button must always be the first button in the first row.
  *
  * @param text                         Label text on the button
  * @param url                          HTTP url to be opened when button is pressed
  * @param callbackData                 Data to be sent in a callback query to the bot when button is pressed, 1-64 bytes
  * @param switchInlineQuery            If set, pressing the button will prompt the user to select one of their chats,
  *                                     open that chat and insert the bot's username and the specified inline query in the input field.
  *                                     Can be empty, in which case just the bot's username will be inserted.
  * @param switchInlineQueryCurrentChat If set, pressing the button will insert the bot's username and the
  *                                     specified inline query in the current chat's input field. Can be empty,
  *                                     in which case only the bot's username will be inserted.
  *                                     This offers a quick way for the user to open your bot in inline mode in the same chat -
  *                                     good for selecting something from multiple options.
  * @param callbackGame                 Description of the game that will be launched when the user presses the button.
  */
final class InlineKeyboardButton private (val text: String,
                                          val callbackData: Option[String] = None,
                                          val url: Option[String] = None,
                                          val loginUrl: Option[LoginUrl] = None,
                                          val switchInlineQuery: Option[String] = None,
                                          val switchInlineQueryCurrentChat: Option[String] = None,
                                          val callbackGame: Option[CallbackGame] = None,
                                          val pay: Option[Boolean] = None)

object InlineKeyboardButton {

  /**
    * Interactive button that will send a callback.
    *
    * @param cbd Data to be sent in a callback query to the bot when button is pressed, 1-64 bytes
    */
  def callbackData(text: String, cbd: String): InlineKeyboardButton =
    new InlineKeyboardButton(text, callbackData = Some(cbd))

  /**
    * Button that opens an URL.
    *
    * @param url HTTP url to be opened when button is pressed
    */
  def url(text: String, url: String): InlineKeyboardButton =
    new InlineKeyboardButton(text, url = Some(url))

  /**
    * Pressing the button will automatically authorize a user.
    * All the user needs to do is tap/click a button and confirm that they want to log in.
    */
  def loginUrl(text: String, loginUrl: LoginUrl): InlineKeyboardButton =
    new InlineKeyboardButton(text, loginUrl = Some(loginUrl))

  /**
    * '''Note:'''
    * This type of button must always be the first button in the first row.
    */
  def callbackGame(text: String): InlineKeyboardButton =
    new InlineKeyboardButton(text, callbackGame = Some(CallbackGame))

  /**
    * Pressing the button will prompt the user to select one of their chats,
    * open that chat and insert the bot's username and the specified inline query in the input field.
    * Can be empty, in which case just the bot's username will be inserted.
    */
  def switchInlineQuery(text: String, siq: String): InlineKeyboardButton =
    new InlineKeyboardButton(text, switchInlineQuery = Some(siq))

  /**
    * Pressing the button will insert the bot's username and the
    * specified inline query in the current chat's input field.
    * Can be empty, in which case only the bot's username will be inserted.
    * This offers a quick way for the user to open your bot in inline mode in the same chat -
    * good for selecting something from multiple options.
    */
  def switchInlineQueryCurrentChat(text: String, siqcc: String): InlineKeyboardButton =
    new InlineKeyboardButton(text, switchInlineQueryCurrentChat = Some(siqcc))

  /**
    * Pay button.
    *
    * '''Note:'''
    * This type of button must always be the first button in the first row.
    */
  def pay(text: String): InlineKeyboardButton =
    new InlineKeyboardButton(text, pay = Some(true))

  implicit val encoderInstance: Encoder[InlineKeyboardButton] = deriveEncoder[InlineKeyboardButton]
}
