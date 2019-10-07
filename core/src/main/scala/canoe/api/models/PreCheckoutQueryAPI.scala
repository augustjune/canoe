package canoe.api.models

import canoe.api.TelegramClient
import canoe.methods.queries.AnswerPreCheckoutQuery
import canoe.models.PreCheckoutQuery
import canoe.syntax.methodOps

final class PreCheckoutQueryAPI[F[_]: TelegramClient](query: PreCheckoutQuery) {

  /**
    * Signals that the bot is ready to proceed with the order.
    */
  def confirm: F[Boolean] =
    AnswerPreCheckoutQuery.positive(query.id).call

  /**
    * Signals that there's a problem with this order.
    *
    * @param message Human readable form that explains the reason for failure
    */
  def reject(message: String): F[Boolean] =
    AnswerPreCheckoutQuery.negative(query.id, message).call
}
