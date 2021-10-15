package canoe.methods.messages

import canoe.marshalling.codecs._
import canoe.methods.Method
import canoe.models.messages.TelegramMessage
import canoe.models.{ChatId, InlineKeyboardMarkup, InputFile, InputMedia}
import io.circe.generic.semiauto
import io.circe.{Decoder, Encoder}

/**
  * Use this method to edit audio, document, photo, or video messages.
  *
  * If a message is a part of a message album, then it can be edited only to a photo or a video.
  * Otherwise, message type can be changed arbitrarily.
  *
  * When inline message is edited, new file can't be uploaded.
  * Use previously uploaded file via its file_id or specify a URL.
  *
  * On success, if the edited message was sent by the bot, the edited Message is returned,
  * otherwise True is returned.
  *
  * Use methods in companion object in order to construct the value of this class.
  *
  * @param chatId          Unique identifier for the target chat or username of the target channel
  *                        (in the format @channelusername).
  *                        Required if 'inlineMessageId' is not specified.
  * @param messageId       Unique identifier of the sent message.
  *                        Required if 'inlineMessageId' is not specified.
  * @param inlineMessageId Identifier of the inline message.
  *                        Required if 'chatId' and 'messageId' are not specified.
  * @param media           New media content of the message
  * @param replyMarkup     New inline keyboard.
  */
final class EditMessageMedia private (val chatId: Option[ChatId],
                                      val messageId: Option[Int],
                                      val inlineMessageId: Option[String],
                                      val media: InputMedia,
                                      val replyMarkup: Option[InlineKeyboardMarkup])

object EditMessageMedia {
  /**
    * For the messages sent directly by the bot.
    */
  def direct(chatId: ChatId,
             messageId: Int,
             media: InputMedia,
             replyMarkup: Option[InlineKeyboardMarkup] = None): EditMessageMedia =
    new EditMessageMedia(Some(chatId), Some(messageId), None, media, replyMarkup)

  /**
    * For the inlined messages sent via the bot.
    */
  def inlined(inlineMessageId: String,
              media: InputMedia,
              replyMarkup: Option[InlineKeyboardMarkup] = None): EditMessageMedia =
    new EditMessageMedia(None, None, Some(inlineMessageId), media, replyMarkup)

  implicit val method: Method[EditMessageMedia, Either[Boolean, TelegramMessage]] =
    new Method[EditMessageMedia, Either[Boolean, TelegramMessage]] {
      import io.circe.generic.auto._

      def name: String = "editMessageMedia"

      def encoder: Encoder[EditMessageMedia] =
        semiauto.deriveEncoder[EditMessageMedia].snakeCase

      def decoder: Decoder[Either[Boolean, TelegramMessage]] =
        Decoder.decodeBoolean.either(TelegramMessage.telegramMessageDecoder)

      def attachments(request: EditMessageMedia): List[(String, InputFile)] =
        request.media.files
    }
}
