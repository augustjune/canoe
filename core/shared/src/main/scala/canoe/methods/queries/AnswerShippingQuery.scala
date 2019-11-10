package canoe.methods.queries

import canoe.marshalling.codecs._
import canoe.methods.Method
import canoe.models.{InputFile, ShippingOption}
import io.circe.generic.semiauto.deriveEncoder
import io.circe.{Decoder, Encoder}

/**
  * If you sent an invoice requesting a shipping address and the parameter is_flexible was specified,
  * the Bot API will send an Update with a shipping_query field to the bot.
  * Use this method to reply to shipping queries.
  *
  * On success, True is returned.
  *
  * Use methods in companion object in order to construct the value of this class.
  *
  * @param shippingQueryId Unique identifier for the query to be answered
  * @param ok              Specify True if delivery to the specified address is possible
  *                        and False if there are any problems
  *                        (for example, if delivery to the specified address is not possible)
  * @param shippingOptions List of available shipping options. Required if ok is True.
  * @param errorMessage    Error message in human readable form that explains why it is impossible to complete the order
  *                        (e.g. "Sorry, delivery to your desired address is unavailable').
  *                        Telegram will display this message to the user.
  *                        Required if ok is False.
  */
final class AnswerShippingQuery private (val shippingQueryId: String,
                                         val ok: Boolean,
                                         val shippingOptions: Option[List[ShippingOption]] = None,
                                         val errorMessage: Option[String] = None)

object AnswerShippingQuery {

  def positive(queryId: String, options: List[ShippingOption]): AnswerShippingQuery =
    new AnswerShippingQuery(queryId, ok = true, shippingOptions = Some(options))

  def negative(queryId: String, message: String): AnswerShippingQuery =
    new AnswerShippingQuery(queryId, ok = false, errorMessage = Some(message))

  implicit val method: Method[AnswerShippingQuery, Boolean] =
    new Method[AnswerShippingQuery, Boolean] {
      import io.circe.generic.auto._

      def name: String = "answerShippingQuery"

      def encoder: Encoder[AnswerShippingQuery] = deriveEncoder[AnswerShippingQuery].snakeCase

      def decoder: Decoder[Boolean] = Decoder.decodeBoolean

      def attachments(request: AnswerShippingQuery): List[(String, InputFile)] = Nil
    }
}
