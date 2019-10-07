package canoe.api.models

import canoe.api.TelegramClient
import canoe.methods.queries.AnswerCallbackQuery
import canoe.models.CallbackQuery
import canoe.syntax.methodOps

class CallbackQueryAPI[F[_]: TelegramClient](query: CallbackQuery) {

  /**
    * Notify the user with the provided text at the top of the chat screen
    */
  def inform(text: String): F[Boolean] =
    AnswerCallbackQuery.notification(query.id, text).call

  /**
    * Notify the user with the provided text in a pop-up form
    */
  def alert(text: String): F[Boolean] =
    AnswerCallbackQuery.alert(query.id, text).call

  /**
    * Redirect the user to the provided address.
    *
    * Example: you can redirect a user to your bot using `telegram.me/your_bot?start=XXXX`
    */
  def redirect(url: String): F[Boolean] =
    AnswerCallbackQuery.redirect(query.id, url).call

}
