package canoe.methods.messages

import canoe.marshalling.codecs._
import canoe.methods.Method
import canoe.models.ParseMode.ParseMode
import canoe.models.messages.TelegramMessage
import canoe.models.{ChatId, InputFile, ReplyMarkup}
import io.circe.generic.semiauto.deriveEncoder
import io.circe.{Decoder, Encoder}

/** Use this method to send photos.
  * On success, the sent Message is returned.
  *
  * @param chatId              Integer or String Unique identifier for the target chat or username of the target channel (in the format @channelusername)
  * @param photo               InputFile or String Photo to send.
  *                            Pass a file_id as String to send a photo that exists on the Telegram servers (recommended),
  *                            pass an HTTP URL as a String for Telegram to get a photo from the Internet, or upload a new photo using multipart/form-data.
  * @param caption             String Optional Photo caption (may also be used when resending photos by file_id), 0-200 characters
  * @param parseMode           String Optional Send Markdown or HTML, if you want Telegram apps to show bold, italic,
  *                            fixed-width text or inline URLs in the media caption.
  * @param disableNotification Boolean Optional Sends the message silently.
  *                            iOS users will not receive a notification, Android users will receive a notification with no sound.
  * @param replyToMessageId    Integer Optional If the message is a reply, ID of the original message
  * @param replyMarkup         InlineKeyboardMarkup or ReplyKeyboardMarkup or ReplyKeyboardHide or ForceReply Optional Additional interface options.
  *                            A JSON-serialized object for an inline keyboard, custom reply keyboard,
  *                            instructions to hide reply keyboard or to force a reply from the user.
  */
case class SendPhoto(chatId: ChatId,
                     photo: InputFile,
                     caption: Option[String] = None,
                     parseMode: Option[ParseMode] = None,
                     disableNotification: Option[Boolean] = None,
                     replyToMessageId: Option[Int] = None,
                     replyMarkup: Option[ReplyMarkup] = None)

object SendPhoto {
  import io.circe.generic.auto._

  implicit val method: Method[SendPhoto, TelegramMessage] =
    new Method[SendPhoto, TelegramMessage] {

      def name: String = "sendPhoto"

      def encoder: Encoder[SendPhoto] = deriveEncoder[SendPhoto].snakeCase

      def decoder: Decoder[TelegramMessage] = TelegramMessage.telegramMessageDecoder

      def uploads(request: SendPhoto): List[(String, InputFile)] =
        List("photo" -> request.photo)
    }
}
