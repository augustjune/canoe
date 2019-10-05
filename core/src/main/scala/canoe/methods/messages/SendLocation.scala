package canoe.methods.messages

import canoe.marshalling.codecs._
import canoe.methods.Method
import canoe.models.messages.TelegramMessage
import canoe.models.{ChatId, InputFile, ReplyMarkup}
import io.circe.generic.semiauto.deriveEncoder
import io.circe.{Decoder, Encoder}

/**
  * Use this method to send point on the map.
  * On success, the sent Message is returned.
  *
  * @param chatId              Unique identifier for the target chat or username of the target channel
  *                            (in the format @channelusername)
  * @param latitude            Latitude of location
  * @param longitude           Longitude of location
  * @param livePeriod          Period in seconds for which the location will be updated (see Live Locations).
  *                            Should be between 60 and 86400.
  * @param disableNotification Sends the message silently.
  *                            iOS users will not receive a notification,
  *                            Android users will receive a notification with no sound.
  * @param replyToMessageId    If the message is a reply, ID of the original message
  * @param replyMarkup         Additional interface options.
  *                            A JSON-serialized object for an inline keyboard, custom reply keyboard,
  *                            instructions to hide reply keyboard or to force a reply from the user.
  */
final case class SendLocation(chatId: ChatId,
                              latitude: Double,
                              longitude: Double,
                              livePeriod: Option[Int] = None,
                              disableNotification: Option[Boolean] = None,
                              replyToMessageId: Option[Int] = None,
                              replyMarkup: Option[ReplyMarkup] = None)

object SendLocation {
  import io.circe.generic.auto._

  implicit val method: Method[SendLocation, TelegramMessage] =
    new Method[SendLocation, TelegramMessage] {

      def name: String = "sendLocation"

      def encoder: Encoder[SendLocation] = deriveEncoder[SendLocation].snakeCase

      def decoder: Decoder[TelegramMessage] = TelegramMessage.telegramMessageDecoder

      def uploads(request: SendLocation): List[(String, InputFile)] = Nil
    }
}
