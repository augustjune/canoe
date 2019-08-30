package canoe.methods.messages

import canoe.marshalling.codecs._
import canoe.methods.Method
import canoe.models.messages.TelegramMessage
import canoe.models.{ChatId, InlineKeyboardMarkup, InputFile}
import io.circe.generic.semiauto.deriveEncoder
import io.circe.{Decoder, Encoder}

/**
  * Use this method to stop updating a live location message sent by the bot or via the bot (for inline bots) before live_period expires.
  * On success, if the message was sent by the bot, the sent Message is returned, otherwise True is returned.
  *
  * @param chatId          Integer or String Optional Required if inline_message_id is not specified.
  *                        Unique identifier for the target chat or username of the target channel (in the format @channelusername)
  * @param messageId       Integer Optional Required if inline_message_id is not specified. Identifier of the sent message
  * @param inlineMessageId String Optional Required if chat_id and message_id are not specified. Identifier of the inline message
  * @param replyMarkup     InlineKeyboardMarkup Optional	A JSON-serialized object for a new inline keyboard.
  */
case class StopMessageLiveLocation(chatId: Option[ChatId] = None,
                                   messageId: Option[Int] = None,
                                   inlineMessageId: Option[Int] = None,
                                   replyMarkup: Option[InlineKeyboardMarkup] = None)

object StopMessageLiveLocation {
  import io.circe.generic.auto._

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
