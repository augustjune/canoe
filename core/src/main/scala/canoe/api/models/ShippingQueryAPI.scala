package canoe.api.models

import canoe.api.TelegramClient
import canoe.methods.queries.AnswerShippingQuery
import canoe.models.{ShippingOption, ShippingQuery}
import canoe.syntax.methodOps

final class ShippingQueryAPI[F[_]: TelegramClient](query: ShippingQuery) {

  /**
    * Signals that the delivery to the specified address is possible
    * and presents available shipping options.
    */
  def proceed(options: List[ShippingOption]): F[Boolean] =
    AnswerShippingQuery.positive(query.id, options).call

  /**
    * Signals that it's impossible to complete the order.
    *
    * @param message Human readable description of the problem
    */
  def abort(message: String): F[Boolean] =
    AnswerShippingQuery.negative(query.id, message).call
}
