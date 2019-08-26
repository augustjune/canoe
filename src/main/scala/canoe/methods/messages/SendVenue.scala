package canoe.methods.messages

import canoe.marshalling.{CirceDecoders, CirceEncoders}
import canoe.methods.{JsonRequest, Method}
import canoe.models.messages.TelegramMessage
import canoe.models.{ChatId, InputFile, ReplyMarkup}
import io.circe.{Decoder, Encoder}

/** Use this method to send information about a venue. On success, the sent Message is returned.
  *
  * @param chatId              Integer or String Unique identifier for the target chat or username of the target channel (in the format @channelusername)
  * @param latitude            Float number Latitude of the venue
  * @param longitude           Float number Longitude of the venue
  * @param title               String Name of the venue
  * @param address             String Address of the venue
  * @param foursquareId        String Optional Foursquare identifier of the venue
  * @param foursquareType      String Optional. Foursquare type of the venue, if known. (For example, “arts_entertainment/default”, “arts_entertainment/aquarium” or “food/icecream”.)
  * @param disableNotification Boolean Optional Sends the message silently.
  *                            iOS users will not receive a notification, Android users will receive a notification with no sound.
  * @param replyToMessageId    Integer Optional If the message is a reply, ID of the original message
  * @param replyMarkup         InlineKeyboardMarkup or ReplyKeyboardMarkup or ReplyKeyboardHide or ForceReply Optional Additional interface options.
  *                            A JSON-serialized object for an inline keyboard, custom reply keyboard,
  *                            instructions to hide reply keyboard or to force a reply from the user.
  */
case class SendVenue(chatId: ChatId,
                     latitude: Double,
                     longitude: Double,
                     title: String,
                     address: String,
                     foursquareId: Option[String] = None,
                     foursquareType: Option[String] = None,
                     duration: Option[String] = None,
                     disableNotification: Option[Boolean] = None,
                     replyToMessageId: Option[Int] = None,
                     replyMarkup: Option[ReplyMarkup] = None
                    ) extends JsonRequest[TelegramMessage]

object SendVenue {

  implicit val method: Method[SendVenue, TelegramMessage] =
    new Method[SendVenue, TelegramMessage] {

      def name: String = "sendVenue"

      def encoder: Encoder[SendVenue] = CirceEncoders.sendVenueEncoder

      def decoder: Decoder[TelegramMessage] = CirceDecoders.telegramMessageDecoder

      def uploads(request: SendVenue): List[(String, InputFile)] = Nil
    }
}
