package canoe.methods.messages

import canoe.marshalling.codecs._
import canoe.methods.Method
import canoe.models.messages.TelegramMessage
import canoe.models.{ChatId, InlineKeyboardMarkup, InputFile}
import io.circe.generic.semiauto.deriveEncoder
import io.circe.{Decoder, Encoder}

/**
  * Use this method to stop updating a live location message
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
                                                  replyMarkup: Option[InlineKeyboardMarkup] = None)

object StopMessageLiveLocation {
  import io.circe.generic.auto._

  /**
    * Use this constructor in order to stop live location of the message sent directly by the bot
    */
  def sentByBot(chatId: ChatId,
                messageId: Int,
                replyMarkup: Option[InlineKeyboardMarkup] = None): StopMessageLiveLocation =
    StopMessageLiveLocation(Some(chatId), Some(messageId), None, replyMarkup)

  /**
    * Use this constructor in order to stop live location of the message sent via the bot as inline message
    */
  def sentViaBot(inlineMessageId: Int, replyMarkup: Option[InlineKeyboardMarkup] = None): StopMessageLiveLocation =
    StopMessageLiveLocation(None, None, Some(inlineMessageId), replyMarkup)

  implicit val method: Method[StopMessageLiveLocation, Either[Boolean, TelegramMessage]] =
    new Method[StopMessageLiveLocation, Either[Boolean, TelegramMessage]] {

      def name: String = "stopMessageLiveLocation"

      def encoder: Encoder[StopMessageLiveLocation] = deriveEncoder[StopMessageLiveLocation].snakeCase

      def decoder: Decoder[Either[Boolean, TelegramMessage]] =
        eitherDecoder(
          Decoder.decodeBoolean,
          TelegramMessage.telegramMessageDecoder
        )

      def uploads(request: StopMessageLiveLocation): List[(String, InputFile)] = Nil
    }
}
