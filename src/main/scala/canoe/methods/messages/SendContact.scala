package canoe.methods.messages

import canoe.marshalling.CirceDecoders
import canoe.marshalling.codecs._
import canoe.methods.Method
import canoe.models.messages.TelegramMessage
import canoe.models.{ChatId, InputFile, ReplyMarkup}
import io.circe.generic.semiauto.deriveEncoder
import io.circe.{Decoder, Encoder}

/** Use this method to send phone contacts.
  * On success, the sent Message is returned.
  *
  * @param chatId              Integer or String Unique identifier for the target chat or username of the target channel (in the format @channelusername)
  * @param phoneNumber         String Contact's phone number
  * @param firstName           String Contact's first name
  * @param lastName            String Optional Contact's last name
  * @param vcard               String Optional Additional data about the contact in the form of a vCard, 0-2048 bytes
  * @param disableNotification Boolean Optional Sends the message silently.
  *                            iOS users will not receive a notification, Android users will receive a notification with no sound.
  * @param replyToMessageId    Integer Optional If the message is a reply, ID of the original message
  * @param replyMarkup         InlineKeyboardMarkup or ReplyKeyboardMarkup or ReplyKeyboardHide or ForceReply Optional Additional interface options.
  *                            A JSON-serialized object for an inline keyboard, custom reply keyboard,
  *                            instructions to hide keyboard or to force a reply from the user.
  *
  */
case class SendContact(chatId: ChatId,
                       phoneNumber: String,
                       firstName: String,
                       lastName: Option[String] = None,
                       vcard: Option[String] = None,
                       disableNotification: Option[Boolean] = None,
                       replyToMessageId: Option[Int] = None,
                       replyMarkup: Option[ReplyMarkup] = None
                      )

object SendContact {
  import io.circe.generic.auto._

  implicit val method: Method[SendContact, TelegramMessage] =
    new Method[SendContact, TelegramMessage] {

      def name: String = "sendContact"

      def encoder: Encoder[SendContact] = deriveEncoder[SendContact].snakeCase

      def decoder: Decoder[TelegramMessage] = CirceDecoders.telegramMessageDecoder

      def uploads(request: SendContact): List[(String, InputFile)] = Nil
    }
}
