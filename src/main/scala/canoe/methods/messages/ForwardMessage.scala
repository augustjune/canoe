package canoe.methods.messages

import canoe.marshalling.{CirceDecoders, CirceEncoders}
import canoe.methods.Method
import canoe.models.messages.TelegramMessage
import canoe.models.{ChatId, InputFile}
import io.circe.{Decoder, Encoder}

/** Use this method to forward messages of any kind. On success, the sent Message is returned.
  *
  * @param chatId              Integer or String Unique identifier for the target chat or username of the target channel (in the format @channelusername)
  * @param fromChatId          Integer or String Unique identifier for the chat where the original message was sent (or channel username in the format @channelusername)
  * @param disableNotification Boolean Optional Sends the message silently. iOS users will not receive a notification, Android users will receive a notification with no sound.
  * @param messageId           Integer Unique message identifier
  */
case class ForwardMessage(chatId: ChatId,
                          fromChatId: ChatId,
                          disableNotification: Option[Boolean] = None,
                          messageId: Int
                         )

object ForwardMessage {

  implicit val method: Method[ForwardMessage, TelegramMessage] =
    new Method[ForwardMessage, TelegramMessage] {

      def name: String = "forwardMessage"

      def encoder: Encoder[ForwardMessage] = CirceEncoders.forwardMessageEncoder

      def decoder: Decoder[TelegramMessage] = CirceDecoders.telegramMessageDecoder

      def uploads(request: ForwardMessage): List[(String, InputFile)] = Nil
    }
}
