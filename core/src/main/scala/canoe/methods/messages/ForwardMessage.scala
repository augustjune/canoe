package canoe.methods.messages

import canoe.marshalling.codecs._
import canoe.methods.Method
import canoe.models.messages.TelegramMessage
import canoe.models.{ChatId, InputFile}
import io.circe.generic.semiauto.deriveEncoder
import io.circe.{Decoder, Encoder}

/**
  * Use this method to forward messages of any kind.
  * On success, the sent Message is returned.
  *
  * @param chatId              Unique identifier for the target chat or username of the target channel
  *                            (in the format @channelusername)
  * @param fromChatId          Unique identifier for the chat where the original message was sent
  *                            (or channel username in the format @channelusername)
  * @param messageId           Unique message identifier
  * @param disableNotification Sends the message silently. iOS users will not receive a notification,
  *                            Android users will receive a notification with no sound.
  */
case class ForwardMessage(chatId: ChatId,
                          fromChatId: ChatId,
                          messageId: Int,
                          disableNotification: Option[Boolean] = None)

object ForwardMessage {

  implicit val method: Method[ForwardMessage, TelegramMessage] =
    new Method[ForwardMessage, TelegramMessage] {

      def name: String = "forwardMessage"

      def encoder: Encoder[ForwardMessage] = deriveEncoder[ForwardMessage].snakeCase

      def decoder: Decoder[TelegramMessage] = TelegramMessage.telegramMessageDecoder

      def uploads(request: ForwardMessage): List[(String, InputFile)] = Nil
    }
}
