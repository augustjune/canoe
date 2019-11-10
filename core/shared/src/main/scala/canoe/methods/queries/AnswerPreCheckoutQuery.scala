package canoe.methods.queries

import canoe.marshalling.codecs._
import canoe.methods.Method
import canoe.models.InputFile
import io.circe.generic.semiauto.deriveEncoder
import io.circe.{Decoder, Encoder}

/**
  * Once the user has confirmed their payment and shipping details,
  * the Bot API sends the final confirmation in the form of an Update with the field pre_checkout_query.
  * Use this method to respond to such pre-checkout queries.
  *
  * On success, True is returned.
  *
  * Use methods in companion object in order to construct the value of this class.
  *
  * '''Note:'''
  *   The Bot API must receive an answer within 10 seconds after the pre-checkout query was sent.
  *
  * @param preCheckoutQueryId  Unique identifier for the query to be answered
  * @param ok                  Specify True if everything is alright (goods are available, etc.)
  *                            and the bot is ready to proceed with the order.
  *                            Use False if there are any problems.
  * @param errorMessage        Error message in human readable form that explains the reason for failure to proceed with the checkout
  *                            (e.g.
  *                              "Sorry, somebody just bought the last of our amazing black T-shirts while you were busy filling out your payment details.
  *                               Please choose a different color or garment!").
  *                            Telegram will display this message to the user.
  *                            Required if ok is False.
  */
final class AnswerPreCheckoutQuery private (val preCheckoutQueryId: String,
                                            val ok: Boolean,
                                            val errorMessage: Option[String] = None)

object AnswerPreCheckoutQuery {

  def positive(queryId: String): AnswerPreCheckoutQuery = new AnswerPreCheckoutQuery(queryId, ok = true)

  def negative(queryId: String, message: String): AnswerPreCheckoutQuery =
    new AnswerPreCheckoutQuery(queryId, ok = false, Some(message))

  implicit val method: Method[AnswerPreCheckoutQuery, Boolean] =
    new Method[AnswerPreCheckoutQuery, Boolean] {

      def name: String = "answerPreCheckoutQuery"

      def encoder: Encoder[AnswerPreCheckoutQuery] = deriveEncoder[AnswerPreCheckoutQuery].snakeCase

      def decoder: Decoder[Boolean] = Decoder.decodeBoolean

      def attachments(request: AnswerPreCheckoutQuery): List[(String, InputFile)] = Nil
    }
}
