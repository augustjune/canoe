package canoe.methods.messages

import canoe.marshalling.codecs._
import canoe.methods.Method
import canoe.models.messages.StickerMessage
import canoe.models.{ChatId, InputFile, ReplyMarkup}
import io.circe.generic.semiauto._
import io.circe.{Decoder, Encoder}

/**
  * Use this method to send stickers.
  * You can send existing stickers (using file_id), static .WEBP or animated .TGS stickers.
  *
  * On success, the sent Message is returned.
  *
  * @param chatId              Unique identifier for the target chat or username of the target channel
  *                            (in the format @channelusername)
  * @param sticker             Sticker to send.
  * @param disableNotification Sends the message silently.
  *                            iOS users will not receive a notification,
  *                            Android users will receive a notification with no sound
  * @param replyToMessageId    If the message is a reply, ID of the original message
  * @param replyMarkup         Additional interface options.
  *                            A JSON-serialized object for an inline keyboard, custom reply keyboard,
  *                            instructions to hide reply keyboard or to force a reply from the user.
  */
final case class SendSticker(chatId: ChatId,
                             sticker: InputFile,
                             disableNotification: Option[Boolean] = None,
                             replyToMessageId: Option[Int] = None,
                             replyMarkup: Option[ReplyMarkup] = None)

object SendSticker {
  import io.circe.generic.auto._

  implicit val method: Method[SendSticker, StickerMessage] =
    new Method[SendSticker, StickerMessage] {

      def name: String = "sendSticker"

      def encoder: Encoder[SendSticker] = deriveEncoder[SendSticker].snakeCase

      def decoder: Decoder[StickerMessage] = deriveDecoder[StickerMessage]

      def attachments(request: SendSticker): List[(String, InputFile)] =
        List("sticker" -> request.sticker)
    }
}
