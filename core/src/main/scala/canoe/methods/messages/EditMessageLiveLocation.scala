package canoe.methods.messages

import canoe.marshalling.codecs._
import canoe.methods.Method
import canoe.models.messages.TelegramMessage
import canoe.models.{ChatId, InlineKeyboardMarkup, InputFile}
import io.circe.generic.semiauto.deriveEncoder
import io.circe.{Decoder, Encoder}

/**
  * Use this method to edit live location messages sent by the bot or via the bot (for inline bots).
  * A location can be edited until its live_period expires or editing is explicitly disabled by a call to stopMessageLiveLocation.
  *
  * Use methods in companion object in order to construct the value of this class.
  *
  * @param chatId          Unique identifier for the target chat or username of the target channel
  *                        (in the format @channelusername)
  *                        Required if 'inlineMessageId' is not specified.
  * @param messageId       Identifier of the sent message.
  *                        Required if 'inlineMessageId' is not specified.
  * @param inlineMessageId Identifier of the inline message.
  *                        Required if 'chatId' and 'messageId' are not specified.
  * @param latitude        Latitude of new location
  * @param longitude       Longitude of new location
  * @param replyMarkup     Additional interface options.
  *                        A JSON-serialized object for an inline keyboard, custom reply keyboard,
  *                        instructions to hide reply keyboard or to force a reply from the user.
  */
final case class EditMessageLiveLocation private (chatId: Option[ChatId],
                                                  messageId: Option[Int],
                                                  inlineMessageId: Option[Int],
                                                  latitude: Double,
                                                  longitude: Double,
                                                  replyMarkup: Option[InlineKeyboardMarkup] = None)

object EditMessageLiveLocation {
  import io.circe.generic.auto._

  /**
    * For the messages sent directed by the bot
    */
  def direct(chatId: ChatId,
             messageId: Int,
             lat: Double,
             long: Double,
             replyMarkup: Option[InlineKeyboardMarkup] = None): EditMessageLiveLocation =
    EditMessageLiveLocation(Some(chatId), Some(messageId), None, lat, long, replyMarkup)

  /**
    * For the inlined messages sent via the bot
    */
  def inlined(inlineMessageId: Int,
              lat: Double,
              long: Double,
              replyMarkup: Option[InlineKeyboardMarkup] = None): EditMessageLiveLocation =
    EditMessageLiveLocation(None, None, Some(inlineMessageId), lat, long, replyMarkup)

  implicit val method: Method[EditMessageLiveLocation, Either[Boolean, TelegramMessage]] =
    new Method[EditMessageLiveLocation, Either[Boolean, TelegramMessage]] {

      def name: String = "editMessageLiveLocation"

      def encoder: Encoder[EditMessageLiveLocation] = deriveEncoder[EditMessageLiveLocation].snakeCase

      def decoder: Decoder[Either[Boolean, TelegramMessage]] =
        eitherDecoder(
          Decoder.decodeBoolean,
          TelegramMessage.telegramMessageDecoder
        )

      def uploads(request: EditMessageLiveLocation): List[(String, InputFile)] = Nil
    }
}
