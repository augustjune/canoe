package canoe.methods.messages

import canoe.marshalling.codecs._
import canoe.methods.Method
import canoe.models.ParseMode.ParseMode
import canoe.models.messages.PhotoMessage
import canoe.models.{ChatId, InputFile, ReplyMarkup}
import io.circe.generic.semiauto
import io.circe.{Decoder, Encoder}

/**
  * Use this method to send photos.
  * On success, the sent Message is returned.
  *
  * @param chatId              Unique identifier for the target chat or username of the target channel
  *                            (in the format @channelusername)
  * @param photo               Photo to send.
  *                            Pass a file_id as String to send a photo that exists on the Telegram servers (recommended),
  *                            pass an HTTP URL as a String for Telegram to get a photo from the Internet, or upload a new photo using multipart/form-data.
  * @param caption             Photo caption (may also be used when resending photos by file_id), 0-1024 characters
  * @param parseMode           Parse mode of captioned text (Markdown or HTML)
  * @param disableNotification Sends the message silently.
  *                            iOS users will not receive a notification,
  *                            Android users will receive a notification with no sound.
  * @param replyToMessageId    If the message is a reply, ID of the original message
  * @param replyMarkup         Additional interface options.
  *                            A JSON-serialized object for an inline keyboard, custom reply keyboard,
  *                            instructions to hide reply keyboard or to force a reply from the user.
  */
final case class SendPhoto(chatId: ChatId,
                           photo: InputFile,
                           caption: Option[String] = None,
                           parseMode: Option[ParseMode] = None,
                           disableNotification: Option[Boolean] = None,
                           replyToMessageId: Option[Int] = None,
                           replyMarkup: Option[ReplyMarkup] = None)

object SendPhoto {
  import io.circe.generic.auto._

  implicit val method: Method[SendPhoto, PhotoMessage] =
    new Method[SendPhoto, PhotoMessage] {

      def name: String = "sendPhoto"

      def encoder: Encoder[SendPhoto] = semiauto.deriveEncoder[SendPhoto].snakeCase

      def decoder: Decoder[PhotoMessage] = semiauto.deriveDecoder[PhotoMessage]

      def attachments(request: SendPhoto): List[(String, InputFile)] =
        List("photo" -> request.photo)
    }
}
