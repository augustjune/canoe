package canoe.methods.messages

import canoe.marshalling.codecs._
import canoe.methods.Method
import canoe.models.ParseMode.ParseMode
import canoe.models.messages.DocumentMessage
import canoe.models.{ChatId, InputFile, ReplyMarkup}
import io.circe.generic.semiauto._
import io.circe.{Decoder, Encoder}

/**
  * Use this method to send general files. On success, the sent Message is returned.
  * Bots can currently send files of any type of up to 50 MB in size, this limit may be changed in the future.
  *
  * @param chatId              Unique identifier for the target chat or username of the target channel
  *                            (in the format @channelusername)
  * @param document            File to send.
  *                            Pass a file_id as String to send a file that exists on the Telegram servers (recommended),
  *                            pass an HTTP URL as a String for Telegram to get a file from the Internet, or upload a new one using multipart/form-data.
  * @param thumb               Thumbnail of the file sent
  * @param caption             Audio caption, 0-1024 characters
  * @param parseMode           Parse mode of captioned text (Markdown or HTML)
  * @param disableNotification Sends the message silently.
  *                            iOS users will not receive a notification,
  *                            Android users will receive a notification with no sound.
  * @param replyToMessageId    If the message is a reply, ID of the original message
  * @param replyMarkup         Additional interface options.
  *                            A JSON-serialized object for an inline keyboard, custom reply keyboard,
  *                            instructions to hide reply keyboard or to force a reply from the user.
  */
final case class SendDocument(chatId: ChatId,
                              document: InputFile,
                              thumb: Option[InputFile] = None,
                              caption: Option[String] = None,
                              parseMode: Option[ParseMode] = None,
                              disableNotification: Option[Boolean] = None,
                              replyToMessageId: Option[Int] = None,
                              replyMarkup: Option[ReplyMarkup] = None)

object SendDocument {
  import io.circe.generic.auto._

  implicit val method: Method[SendDocument, DocumentMessage] =
    new Method[SendDocument, DocumentMessage] {

      def name: String = "sendDocument"

      def encoder: Encoder[SendDocument] = deriveEncoder[SendDocument].snakeCase

      def decoder: Decoder[DocumentMessage] = deriveDecoder[DocumentMessage]

      def attachments(request: SendDocument): List[(String, InputFile)] =
        List("document" -> request.document)
    }
}
