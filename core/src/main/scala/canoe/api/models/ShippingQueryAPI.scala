package canoe.api.models

import canoe.api.TelegramClient
import canoe.methods.queries.AnswerShippingQuery
import canoe.models.{ShippingOption, ShippingQuery}
import canoe.syntax.methodOps

/**
  * Telegram API for the shipping query.
  * Offers a convenient access to the related Telegram methods in OO style.
  *
  * It is a conscious decision to provide this API via extension methods.
  */
final class ShippingQueryAPI(private val query: ShippingQuery) extends AnyVal {

  /**
    * Signals that the delivery to the specified address is possible
    * and presents available shipping options.
    */
  def proceed[F[_]: TelegramClient](options: List[ShippingOption]): F[Boolean] =
    AnswerShippingQuery.positive(query.id, options).call

  /**
    * Signals that it's impossible to complete the order.
    *
    * @param message Human readable description of the problem
    */
  def abort[F[_]: TelegramClient](message: String): F[Boolean] =
    AnswerShippingQuery.negative(query.id, message).call
}
