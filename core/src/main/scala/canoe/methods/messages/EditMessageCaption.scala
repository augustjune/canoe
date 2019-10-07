package canoe.methods.messages

import canoe.marshalling.codecs._
import canoe.methods.Method
import canoe.models.ParseMode.ParseMode
import canoe.models.messages.TelegramMessage
import canoe.models.{ChatId, InputFile, ReplyMarkup}
import io.circe.generic.auto._
import io.circe.generic.semiauto.deriveEncoder
import io.circe.{Decoder, Encoder}

/**
  * Use this method to edit captions of messages sent by the bot or via the bot (for inline bots).
  *
  * On success, if edited message is sent by the bot, the edited Message is returned,
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
  * @param caption         New caption of the message
  * @param parseMode       Parse mode of input text (Markdown or HTML)
  * @param replyMarkup     Additional interface options.
  *                        A JSON-serialized object for an inline keyboard, custom reply keyboard,
  *                        instructions to hide reply keyboard or to force a reply from the user.
  */
final class EditMessageCaption private (val chatId: Option[ChatId],
                                        val messageId: Option[Int],
                                        val inlineMessageId: Option[String],
                                        val caption: Option[String] = None,
                                        val parseMode: Option[ParseMode] = None,
                                        val replyMarkup: Option[ReplyMarkup] = None)

object EditMessageCaption {

  /**
    * For the messages sent directed by the bot
    */
  def direct(chatId: ChatId,
             messageId: Int,
             caption: Option[String],
             parseMode: Option[ParseMode] = None,
             disableWebPagePreview: Option[Boolean] = None,
             replyMarkup: Option[ReplyMarkup] = None): EditMessageCaption =
    new EditMessageCaption(Some(chatId), Some(messageId), None, caption, parseMode, replyMarkup)

  /**
    * For the inlined messages sent via the bot
    */
  def inlined(inlineMessageId: String,
              caption: Option[String],
              parseMode: Option[ParseMode] = None,
              disableWebPagePreview: Option[Boolean] = None,
              replyMarkup: Option[ReplyMarkup] = None): EditMessageCaption =
    new EditMessageCaption(None, None, Some(inlineMessageId), caption, parseMode, replyMarkup)

  implicit val method: Method[EditMessageCaption, Either[Boolean, TelegramMessage]] =
    new Method[EditMessageCaption, Either[Boolean, TelegramMessage]] {

      def name: String = "editMessageCaption"

      def encoder: Encoder[EditMessageCaption] = deriveEncoder[EditMessageCaption].snakeCase

      def decoder: Decoder[Either[Boolean, TelegramMessage]] =
        eitherDecoder(
          Decoder.decodeBoolean,
          TelegramMessage.telegramMessageDecoder
        )

      def uploads(request: EditMessageCaption): List[(String, InputFile)] = Nil
    }
}
