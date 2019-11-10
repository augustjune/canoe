package canoe.methods.chats

import canoe.marshalling.codecs._
import canoe.methods.Method
import canoe.models.{ChatId, InputFile}
import io.circe.generic.semiauto.deriveEncoder
import io.circe.{Decoder, Encoder}

/**
  * Use this method to pin a message in a group, a supergroup, or a channel.
  *
  * The bot must be an administrator in the chat for this to work and
  * must have the ‘can_pin_messages’ admin right in the supergroup
  * or ‘can_edit_messages’ admin right in the channel.
  *
  * @param chatId              Unique identifier for the target chat or username of the target channel
  *                            (in the format @channelusername)
  * @param messageId           Identifier of a message to pin
  * @param disableNotification True, if it is not necessary to send a notification to all chat members about the new pinned message.
  *                            Notifications are always disabled in channels.
  */
final case class PinChatMessage(chatId: ChatId, messageId: Int, disableNotification: Option[Boolean] = None)

object PinChatMessage {

  implicit val method: Method[PinChatMessage, Boolean] =
    new Method[PinChatMessage, Boolean] {

      def name: String = "pinChatMessage"

      def encoder: Encoder[PinChatMessage] = deriveEncoder[PinChatMessage].snakeCase

      def decoder: Decoder[Boolean] = Decoder.decodeBoolean

      def attachments(request: PinChatMessage): List[(String, InputFile)] = Nil
    }
}
