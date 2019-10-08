package canoe.api.models

import canoe.api.TelegramClient
import canoe.methods.queries.AnswerCallbackQuery
import canoe.models.CallbackQuery
import canoe.syntax.methodOps

/**
  * Telegram API for the callback query.
  * Offers a convenient access to the related Telegram methods in OO style.
  *
  * It is a conscious decision to provide this API via extension methods.
  */
final class CallbackQueryAPI(private val query: CallbackQuery) extends AnyVal {

  /**
    * Notify the user with the provided text at the top of the chat screen
    */
  def inform[F[_]: TelegramClient](text: String): F[Boolean] =
    AnswerCallbackQuery.notification(query.id, text).call

  /**
    * Notify the user with the provided text in a pop-up form
    */
  def alert[F[_]: TelegramClient](text: String): F[Boolean] =
    AnswerCallbackQuery.alert(query.id, text).call

  /**
    * Redirect the user to the provided address.
    *
    * Example: you can redirect a user to your bot using `telegram.me/your_bot?start=XXXX`
    */
  def redirect[F[_]: TelegramClient](url: String): F[Boolean] =
    AnswerCallbackQuery.redirect(query.id, url).call

}
