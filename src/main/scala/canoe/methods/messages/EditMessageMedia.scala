package canoe.methods.messages

import canoe.marshalling.CirceDecoders
import canoe.marshalling.codecs._
import canoe.methods.Method
import canoe.models.messages.TelegramMessage
import canoe.models.{ChatId, InlineKeyboardMarkup, InputFile, InputMedia}
import io.circe.generic.semiauto.deriveEncoder
import io.circe.{Decoder, Encoder}

/** Use this method to edit audio, document, photo, or video messages.
  * If a message is a part of a message album, then it can be edited only to a photo or a video.
  * Otherwise, message type can be changed arbitrarily.
  * When inline message is edited, new file can't be uploaded.
  * Use previously uploaded file via its file_id or specify a URL.
  * On success, if the edited message was sent by the bot, the edited Message is returned, otherwise True is returned.
  *
  * @param chatId          Optional Required if inline_message_id is not specified. Unique identifier for the target chat or username of the target channel (in the format @channelusername)
  * @param messageId       Integer 	Optional Required if inline_message_id is not specified. Identifier of the sent message
  * @param inlineMessageId String Optional 	Required if chat_id and message_id are not specified. Identifier of the inline message
  * @param media           InputMedia 	Yes 	A JSON-serialized object for a new media content of the message
  * @param replyMarkup     InlineKeyboardMarkup Optional A JSON-serialized object for a new inline keyboard.
  */
case class EditMessageMedia(chatId: Option[ChatId] = None,
                            messageId: Option[Int] = None,
                            inlineMessageId: Option[String] = None,
                            media: InputMedia,
                            replyMarkup: Option[InlineKeyboardMarkup] = None
                           )

object EditMessageMedia {
  import io.circe.generic.auto._

  implicit val method: Method[EditMessageMedia, Either[Boolean, TelegramMessage]] =
    new Method[EditMessageMedia, Either[Boolean, TelegramMessage]] {

      def name: String = "editMessageMedia"

      def encoder: Encoder[EditMessageMedia] = deriveEncoder[EditMessageMedia].snakeCase

      def decoder: Decoder[Either[Boolean, TelegramMessage]] =
      // ToDo - set keys
        Decoder.decodeEither("", "")(
          Decoder.decodeBoolean,
          CirceDecoders.telegramMessageDecoder
        )

      def uploads(request: EditMessageMedia): List[(String, InputFile)] =
        request.media.files
    }
}
