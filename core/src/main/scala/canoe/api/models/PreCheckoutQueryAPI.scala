package canoe.api.models

import canoe.api.TelegramClient
import canoe.methods.queries.AnswerPreCheckoutQuery
import canoe.models.PreCheckoutQuery
import canoe.syntax.methodOps

/**
  * Telegram API for the pre checkout query.
  * Offers a convenient access to the related Telegram methods.
  */
final class PreCheckoutQueryAPI(private val query: PreCheckoutQuery) extends AnyVal {

  /**
    * Signals that the bot is ready to proceed with the order.
    */
  def confirm[F[_]: TelegramClient]: F[Boolean] =
    AnswerPreCheckoutQuery.positive(query.id).call

  /**
    * Signals that there's a problem with this order.
    *
    * @param message Human readable form that explains the reason for failure
    */
  def reject[F[_]: TelegramClient](message: String): F[Boolean] =
    AnswerPreCheckoutQuery.negative(query.id, message).call
}
