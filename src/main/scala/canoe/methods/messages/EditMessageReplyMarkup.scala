package canoe.methods.messages

import canoe.marshalling.{CirceDecoders, CirceEncoders}
import canoe.methods.Method
import canoe.models.messages.TelegramMessage
import canoe.models.{ChatId, InlineKeyboardMarkup, InputFile}
import io.circe.{Decoder, Encoder}

/** Use this method to edit only the reply markup of messages sent by the bot or via the bot (for inline bots).
  * On success, if edited message is sent by the bot, the edited Message is returned, otherwise True is returned.
  *
  * @param chatId          Integer or String Required if inline_message_id is not specified. Unique identifier for the target chat or username of the target channel (in the format @channelusername)
  * @param messageId       Integer Required if inline_message_id is not specified. Unique identifier of the sent message
  * @param inlineMessageId String Required if chat_id and message_id are not specified. Identifier of the inline message
  * @param replyMarkup     InlineKeyboardMarkup Optional A JSON-serialized object for an inline keyboard.
  */
case class EditMessageReplyMarkup(chatId: Option[ChatId] = None,
                                  messageId: Option[Int] = None,
                                  inlineMessageId: Option[String] = None,
                                  replyMarkup: Option[InlineKeyboardMarkup] = None
                                 ) {
  if (inlineMessageId.isEmpty) {
    require(chatId.isDefined, "Required if inlineMessageId is not specified")
    require(messageId.isDefined, "Required if inlineMessageId is not specified")
  }

  if (chatId.isEmpty && messageId.isEmpty)
    require(inlineMessageId.isDefined, "Required if chatId and messageId are not specified")
}

object EditMessageReplyMarkup {

  implicit val method: Method[EditMessageReplyMarkup, Either[Boolean, TelegramMessage]] =
    new Method[EditMessageReplyMarkup, Either[Boolean, TelegramMessage]] {

      def name: String = "editMessageReplyMarkup"

      def encoder: Encoder[EditMessageReplyMarkup] = CirceEncoders.editMessageReplyMarkupEncoder

      def decoder: Decoder[Either[Boolean, TelegramMessage]] =
      // ToDo - set keys
        Decoder.decodeEither("", "")(
          Decoder.decodeBoolean,
          CirceDecoders.telegramMessageDecoder
        )

      def uploads(request: EditMessageReplyMarkup): List[(String, InputFile)] = Nil
    }
}
