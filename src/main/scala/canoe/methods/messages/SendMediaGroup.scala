package canoe.methods.messages

import canoe.marshalling.{CirceDecoders, CirceEncoders}
import canoe.methods.Method
import canoe.models.messages.TelegramMessage
import canoe.models.{ChatId, InputFile, InputMedia}
import io.circe.{Decoder, Encoder}

/**
  * Use this method to send a group of photos or videos as an album.
  * On success, an array of the sent Messages is returned.
  *
  * @param chatId              Integer or String Unique identifier for the target chat or username of the target channel (in the format @channelusername)
  * @param media               Array of InputMedia A JSON-serialized array describing photos and videos to be sent, must include 2–10 items
  * @param disableNotification Boolean Optional Sends the messages silently. Users will receive a notification with no sound.
  * @param replyToMessageId    Integer Optional If the messages are a reply, ID of the original message
  */
case class SendMediaGroup(chatId: ChatId,
                          media: List[InputMedia],
                          disableNotification: Option[Boolean] = None,
                          replyToMessageId: Option[Int] = None)

object SendMediaGroup {

  implicit val method: Method[SendMediaGroup, List[TelegramMessage]] =
    new Method[SendMediaGroup, List[TelegramMessage]] {

      def name: String = "sendMediaGroup"

      def encoder: Encoder[SendMediaGroup] =
        CirceEncoders.sendMediaGroupEncoder

      def decoder: Decoder[List[TelegramMessage]] =
        Decoder.decodeList(CirceDecoders.telegramMessageDecoder)

      def uploads(request: SendMediaGroup): List[(String, InputFile)] =
        request.media.flatMap(_.files)
    }
}
