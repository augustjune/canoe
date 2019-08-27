package canoe.methods.messages

import canoe.marshalling.{CirceDecoders, CirceEncoders}
import canoe.methods.Method
import canoe.models.ParseMode.ParseMode
import canoe.models.messages.TelegramMessage
import canoe.models.{ChatId, InputFile, ReplyMarkup}
import io.circe.{Decoder, Encoder}

/** Use this method to edit captions of messages sent by the bot or via the bot (for inline bots).
  * On success, if edited message is sent by the bot, the edited Message is returned, otherwise True is returned.
  *
  * @param chatId          Integer or String No Required if inline_message_id is not specified. Unique identifier for the target chat or username of the target channel (in the format @channelusername)
  * @param messageId       Integer No Required if inline_message_id is not specified. Unique identifier of the sent message
  * @param inlineMessageId String No Required if chat_id and message_id are not specified. Identifier of the inline message
  * @param caption         String Optional New caption of the message
  * @param parseMode       String Optional Send Markdown or HTML, if you want Telegram apps to show bold, italic,
  *                        fixed-width text or inline URLs in the media caption.
  * @param replyMarkup     InlineKeyboardMarkup Optional A JSON-serialized object for an inline keyboard.
  */
case class EditMessageCaption(chatId: Option[ChatId] = None,
                              messageId: Option[Int] = None,
                              inlineMessageId: Option[String] = None,
                              caption: Option[String] = None,
                              parseMode: Option[ParseMode] = None,
                              replyMarkup: Option[ReplyMarkup] = None
                             ) {

  if (inlineMessageId.isEmpty) {
    require(chatId.isDefined, "Required if inlineMessageId is not specified")
    require(messageId.isDefined, "Required if inlineMessageId is not specified")
  }

  if (chatId.isEmpty && messageId.isEmpty)
    require(inlineMessageId.isDefined, "Required if chatId and messageId are not specified")
}

object EditMessageCaption {

  implicit val method: Method[EditMessageCaption, Either[Boolean, TelegramMessage]] =
    new Method[EditMessageCaption, Either[Boolean, TelegramMessage]] {

      def name: String = "editMessageCaption"

      def encoder: Encoder[EditMessageCaption] = CirceEncoders.editMessageCaptionEncoder

      def decoder: Decoder[Either[Boolean, TelegramMessage]] =
      // ToDo - set keys
        Decoder.decodeEither("", "")(
          Decoder.decodeBoolean,
          CirceDecoders.telegramMessageDecoder
        )

      def uploads(request: EditMessageCaption): List[(String, InputFile)] = Nil
    }
}
