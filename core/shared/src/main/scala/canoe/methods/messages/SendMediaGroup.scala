package canoe.methods.messages

import canoe.marshalling.codecs._
import canoe.methods.Method
import canoe.models.messages.TelegramMessage
import canoe.models.{ChatId, InputFile, InputMedia, InputMediaPhoto}
import io.circe.generic.semiauto.deriveEncoder
import io.circe.{Decoder, Encoder}

/**
  * Use this method to send a group of photos or videos as an album.
  * On success, an array of the sent Messages is returned.
  *
  * @param chatId              Unique identifier for the target chat or username of the target channel
  *                            (in the format @channelusername)
  * @param media               List of photos and videos to be sent, must include 2–10 items
  * @param disableNotification Sends the message silently.
  *                            iOS users will not receive a notification,
  *                            Android users will receive a notification with no sound
  * @param replyToMessageId    If the message is a reply, ID of the original message
  */
final case class SendMediaGroup(chatId: ChatId,
                                media: List[InputMedia],
                                disableNotification: Option[Boolean] = None,
                                replyToMessageId: Option[Int] = None)

object SendMediaGroup {

  implicit val method: Method[SendMediaGroup, List[TelegramMessage]] =
    new Method[SendMediaGroup, List[TelegramMessage]] {

      def name: String = "sendMediaGroup"

      def encoder: Encoder[SendMediaGroup] =
        deriveEncoder[SendMediaGroup]
          .snakeCase

      def decoder: Decoder[List[TelegramMessage]] =
        Decoder.decodeList(TelegramMessage.telegramMessageDecoder)

      def attachments(request: SendMediaGroup): List[(String, InputFile)] = {
        request.media.flatMap {
          case x: InputMediaPhoto =>
            val name = x.media match {
              case InputFile.Upload(filename, _) => filename
              case _ => x.`type`
            }
            List(name -> x.media)
          case x => x.files
        }
      }
    }
}
