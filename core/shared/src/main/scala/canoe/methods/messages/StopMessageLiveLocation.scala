package canoe.methods.messages

import canoe.marshalling.codecs._
import canoe.methods.Method
import canoe.models.messages.TelegramMessage
import canoe.models.{ChatId, InlineKeyboardMarkup, InputFile}
import io.circe.generic.semiauto
import io.circe.{Decoder, Encoder}

/** Use this method to stop updating a live location message
  * sent by the bot or via the bot (for inline bots) before live_period expires.
  *
  * Use methods in companion object in order to construct the value of this class.
  *
  * @param chatId          Unique identifier for the target chat or username of the target channel
  *                        (in the format @channelusername)
  *                        Required if 'inlineMessageId' is not specified.
  * @param messageId       Identifier of the sent message.
  *                        Required if 'inlineMessageId' is not specified.
  * @param inlineMessageId Identifier of the inline message.
  *                        Required if 'chatId' and 'messageId' are not specified.
  * @param replyMarkup     Additional interface options.
  *                        A JSON-serialized object for an inline keyboard, custom reply keyboard,
  *                        instructions to hide reply keyboard or to force a reply from the user.
  */
final case class StopMessageLiveLocation private (chatId: Option[ChatId],
                                                  messageId: Option[Int],
                                                  inlineMessageId: Option[Int],
                                                  replyMarkup: Option[InlineKeyboardMarkup]
)

object StopMessageLiveLocation {

  /** For the messages sent directly by the bot
    */
  def direct(chatId: ChatId,
             messageId: Int,
             replyMarkup: Option[InlineKeyboardMarkup] = None
  ): StopMessageLiveLocation =
    new StopMessageLiveLocation(Some(chatId), Some(messageId), None, replyMarkup)

  /** For the inlined messages sent via the bot
    */
  def inlined(inlineMessageId: Int, replyMarkup: Option[InlineKeyboardMarkup] = None): StopMessageLiveLocation =
    new StopMessageLiveLocation(None, None, Some(inlineMessageId), replyMarkup)

  implicit val method: Method[StopMessageLiveLocation, Either[Boolean, TelegramMessage]] =
    new Method[StopMessageLiveLocation, Either[Boolean, TelegramMessage]] {
      import io.circe.generic.auto._

      def name: String = "stopMessageLiveLocation"

      def encoder: Encoder[StopMessageLiveLocation] = semiauto.deriveEncoder[StopMessageLiveLocation].snakeCase

      def decoder: Decoder[Either[Boolean, TelegramMessage]] =
        Decoder.decodeBoolean.either(TelegramMessage.telegramMessageDecoder)

      def attachments(request: StopMessageLiveLocation): List[(String, InputFile)] = Nil
    }
}
