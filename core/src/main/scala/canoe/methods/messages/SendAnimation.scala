package canoe.methods.messages

import canoe.marshalling.codecs._
import canoe.methods.Method
import canoe.models.ParseMode.ParseMode
import canoe.models.messages.TelegramMessage
import canoe.models.{ChatId, InputFile, ReplyMarkup}
import io.circe.generic.semiauto.deriveEncoder
import io.circe.{Decoder, Encoder}

/**
  * Use this method to send animation files (GIF or H.264/MPEG-4 AVC video without sound).
  *
  * On success, the sent Message is returned.
  * Bots can currently send animation files of up to 50 MB in size, this limit may be changed in the future.
  *
  * @param chatId              Unique identifier for the target chat or username of the target channel
  *                            (in the format @channelusername)
  * @param animation           Animation to send
  * @param duration            Duration of sent animation in seconds
  * @param width               Animation width
  * @param height              Animation height
  * @param thumb               Thumbnail of the file sent
  * @param caption             Video caption (may also be used when resending videos by file_id), 0-200 characters
  * @param parseMode           Parse mode of captured text (Markdown or HTML)
  * @param disableNotification Sends the message silently.
  *                            iOS users will not receive a notification,
  *                            Android users will receive a notification with no sound
  * @param replyToMessageId    If the message is a reply, ID of the original message
  * @param replyMarkup         Additional interface options.
  *                            A JSON-serialized object for an inline keyboard, custom reply keyboard,
  *                            instructions to hide reply keyboard or to force a reply from the user.
  */
case class SendAnimation(chatId: ChatId,
                         animation: InputFile,
                         duration: Option[Int] = None,
                         width: Option[Int] = None,
                         height: Option[Int] = None,
                         thumb: Option[InputFile] = None,
                         caption: Option[String] = None,
                         parseMode: Option[ParseMode] = None,
                         disableNotification: Option[Boolean] = None,
                         replyToMessageId: Option[Int] = None,
                         replyMarkup: Option[ReplyMarkup] = None)

object SendAnimation {
  import io.circe.generic.auto._

  implicit val method: Method[SendAnimation, TelegramMessage] =
    new Method[SendAnimation, TelegramMessage] {

      def name: String = "sendAnimation"

      def encoder: Encoder[SendAnimation] = deriveEncoder[SendAnimation].snakeCase

      def decoder: Decoder[TelegramMessage] = TelegramMessage.telegramMessageDecoder

      def uploads(request: SendAnimation): List[(String, InputFile)] =
        List("animation" -> request.animation) ++ request.thumb.map(t => "thumb" -> t).toList
    }
}
