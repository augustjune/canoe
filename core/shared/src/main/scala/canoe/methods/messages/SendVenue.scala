package canoe.methods.messages

import canoe.marshalling.codecs._
import canoe.methods.Method
import canoe.models.messages.VenueMessage
import canoe.models.{ChatId, InputFile, ReplyMarkup}
import io.circe.generic.semiauto
import io.circe.{Decoder, Encoder}

/**
  * Use this method to send information about a venue. On success, the sent Message is returned.
  *
  * @param chatId              Unique identifier for the target chat or username of the target channel
  *                            (in the format @channelusername)
  * @param latitude            Latitude of the venue
  * @param longitude           Longitude of the venue
  * @param title               Name of the venue
  * @param address             Address of the venue
  * @param foursquareId        Foursquare identifier of the venue
  * @param foursquareType      Foursquare type of the venue, if known. (For example, “arts_entertainment/default”, “arts_entertainment/aquarium” or “food/icecream”.)
  * @param disableNotification Sends the message silently.
  *                            iOS users will not receive a notification,
  *                            Android users will receive a notification with no sound
  * @param replyToMessageId    If the message is a reply, ID of the original message
  * @param replyMarkup         Additional interface options.
  *                            A JSON-serialized object for an inline keyboard, custom reply keyboard,
  *                            instructions to hide reply keyboard or to force a reply from the user.
  */
final case class SendVenue(chatId: ChatId,
                           latitude: Double,
                           longitude: Double,
                           title: String,
                           address: String,
                           foursquareId: Option[String] = None,
                           foursquareType: Option[String] = None,
                           disableNotification: Option[Boolean] = None,
                           replyToMessageId: Option[Int] = None,
                           replyMarkup: Option[ReplyMarkup] = None)

object SendVenue {
  import io.circe.generic.auto._

  implicit val method: Method[SendVenue, VenueMessage] =
    new Method[SendVenue, VenueMessage] {

      def name: String = "sendVenue"

      def encoder: Encoder[SendVenue] = semiauto.deriveEncoder[SendVenue].snakeCase

      def decoder: Decoder[VenueMessage] = semiauto.deriveDecoder[VenueMessage]

      def attachments(request: SendVenue): List[(String, InputFile)] = Nil
    }
}
