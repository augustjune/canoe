package canoe.methods.messages

import canoe.marshalling.codecs._
import canoe.methods.Method
import canoe.models.messages.TelegramMessage
import canoe.models.{ChatId, InlineKeyboardMarkup, InputFile}
import io.circe.generic.semiauto.deriveEncoder
import io.circe.{Decoder, Encoder}

/**
  * Use this method to edit live location messages sent by the bot or via the bot (for inline bots).
  * A location can be edited until its live_period expires or editing is explicitly disabled by a call to stopMessageLiveLocation.
  * On success, if the edited message was sent by the bot,
  * the edited Message is returned, otherwise True is returned.
  *
  * @param chatId           Integer or String Optional	Required if inline_message_id is not specified. Unique identifier for the target chat or username of the target channel (in the format @channelusername)
  * @param messageId        Integer	Optional Required if inline_message_id is not specified. Identifier of the sent message
  * @param inlineMessageId  String	Optional Required if chat_id and message_id are not specified. Identifier of the inline message
  * @param latitude         Float number Yes Latitude of new location
  * @param longitude        Float number Yes	Longitude of new location
  * @param replyMarkup      InlineKeyboardMarkup Optional A JSON-serialized object for a new inline keyboard.
  */
case class EditMessageLiveLocation(chatId          : Option[ChatId] = None,
                                   messageId       : Option[Int] = None,
                                   inlineMessageId : Option[Int] = None,
                                   latitude        : Option[Double] = None,
                                   longitude       : Option[Double] = None,
                                   replyMarkup     : Option[InlineKeyboardMarkup] = None
                                  )

object EditMessageLiveLocation {
  import io.circe.generic.auto._

  implicit val method: Method[EditMessageLiveLocation, Either[Boolean, TelegramMessage]] =
    new Method[EditMessageLiveLocation, Either[Boolean, TelegramMessage]] {

      def name: String = "editMessageLiveLocation"

      def encoder: Encoder[EditMessageLiveLocation] = deriveEncoder[EditMessageLiveLocation].snakeCase

      def decoder: Decoder[Either[Boolean, TelegramMessage]] =
        eitherDecoder(
          Decoder.decodeBoolean,
          TelegramMessage.telegramMessageDecoder
        )

      def uploads(request: EditMessageLiveLocation): List[(String, InputFile)] = Nil
    }
}
