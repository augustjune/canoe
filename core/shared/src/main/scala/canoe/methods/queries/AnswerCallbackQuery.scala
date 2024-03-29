package canoe.methods.queries

import canoe.marshalling.codecs._
import canoe.methods.Method
import canoe.models.InputFile
import io.circe.generic.semiauto
import io.circe.{Decoder, Encoder}

/** Use this method to send answers to callback queries sent from inline keyboards.
  *
  * The answer will be displayed to the user as a notification at the top of the chat screen or as an alert.
  *
  * On success, True is returned.
  *
  * Alternatively, the user can be redirected to the specified Game URL.
  * For this option to work, you must first create a game for your bot via BotFather and accept the terms.
  * Otherwise, you may use links like telegram.me/your_bot?start=XXXX that open your bot with a parameter.
  *
  * @param callbackQueryId Unique identifier for the query to be answered
  * @param text            Text of the notification. 0-200 characters.
  *                        If not specified, nothing will be shown to the user.
  * @param showAlert       If true, an alert will be shown by the client instead
  *                        of a notification at the top of the chat screen.
  *                        Defaults to false.
  * @param url             URL that will be opened by the user's client.
  *                        If you have created a Game and accepted the conditions via @Botfather,
  *                        specify the URL that opens your game.
  *                        Note that this will only work if the query comes from a callback_game button.
  *                        Otherwise, you may use links like telegram.me/your_bot?start=XXXX that open your bot with a parameter.
  * @param cacheTime       The maximum amount of time in seconds that the result of the callback query may be cached client-side.
  *                        Telegram apps will support caching starting in version 3.14.
  *                        Defaults to 0.
  */
final case class AnswerCallbackQuery private (callbackQueryId: String,
                                              text: Option[String] = None,
                                              showAlert: Option[Boolean] = None,
                                              url: Option[String] = None,
                                              cacheTime: Option[Int] = None
)

object AnswerCallbackQuery {

  /** Notification answer which shows provided text at the top of the chat screen
    */
  def notification(queryId: String, text: String): AnswerCallbackQuery =
    new AnswerCallbackQuery(queryId, text = Some(text))

  /** Alert answer which shows provided text in a pop-up form
    */
  def alert(queryId: String, text: String): AnswerCallbackQuery =
    new AnswerCallbackQuery(queryId, text = Some(text), showAlert = Some(true))

  /** React without notification to stop a progress bar
    */
  def finish(queryId: String): AnswerCallbackQuery =
    new AnswerCallbackQuery(queryId)

  /** Answer which redirects the user to the provided address.
    * Example: you can redirect a user to your bot using `telegram.me/your_bot?start=XXXX`
    */
  def redirect(queryId: String, url: String): AnswerCallbackQuery =
    new AnswerCallbackQuery(queryId, url = Some(url))

  implicit val method: Method[AnswerCallbackQuery, Boolean] =
    new Method[AnswerCallbackQuery, Boolean] {

      def name: String = "answerCallbackQuery"

      def encoder: Encoder[AnswerCallbackQuery] = semiauto.deriveEncoder[AnswerCallbackQuery].snakeCase

      def decoder: Decoder[Boolean] = Decoder.decodeBoolean

      def attachments(request: AnswerCallbackQuery): List[(String, InputFile)] = Nil
    }
}
