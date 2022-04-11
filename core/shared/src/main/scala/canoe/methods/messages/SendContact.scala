package canoe.methods.messages

import canoe.marshalling.codecs._
import canoe.methods.Method
import canoe.models.messages.ContactMessage
import canoe.models.{ChatId, InputFile, ReplyMarkup}
import io.circe.generic.semiauto
import io.circe.{Decoder, Encoder}

/**
  * Use this method to send phone contacts.
  * On success, the sent Message is returned.
  *
  * @param chatId              Unique identifier for the target chat or username of the target channel
  *                            (in the format @channelusername)
  * @param phoneNumber         Contact's phone number
  * @param firstName           Contact's first name
  * @param lastName            Contact's last name
  * @param vcard               Additional data about the contact in the form of a vCard, 0-2048 bytes
  * @param disableNotification Sends the message silently.
  *                            iOS users will not receive a notification,
  *                            Android users will receive a notification with no sound
  * @param replyToMessageId    If the message is a reply, ID of the original message
  * @param replyMarkup         Additional interface options.
  *                            A JSON-serialized object for an inline keyboard, custom reply keyboard,
  *                            instructions to hide reply keyboard or to force a reply from the user.
  *
  */
final case class SendContact(chatId: ChatId,
                             phoneNumber: String,
                             firstName: String,
                             lastName: Option[String] = None,
                             vcard: Option[String] = None,
                             disableNotification: Option[Boolean] = None,
                             replyToMessageId: Option[Int] = None,
                             replyMarkup: Option[ReplyMarkup] = None)

object SendContact {
  import io.circe.generic.auto._

  implicit val method: Method[SendContact, ContactMessage] =
    new Method[SendContact, ContactMessage] {

      def name: String = "sendContact"

      def encoder: Encoder[SendContact] = semiauto.deriveEncoder[SendContact].snakeCase

      def decoder: Decoder[ContactMessage] = semiauto.deriveDecoder[ContactMessage]

      def attachments(request: SendContact): List[(String, InputFile)] = Nil
    }
}
