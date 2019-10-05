package canoe.methods.chats

import canoe.marshalling.codecs._
import canoe.methods.Method
import canoe.models.{ChatId, InputFile}
import io.circe.generic.semiauto.deriveEncoder
import io.circe.{Decoder, Encoder}

/**
  * Use this method to change the description of a supergroup or a channel.
  *
  * The bot must be an administrator in the chat for this to work and
  * must have the appropriate admin rights.
  *
  * Returns True on success.
  *
  * @param chatId      Unique identifier for the target chat or username of the target channel
  *                    (in the format @channelusername)
  * @param description New chat description, 0-255 characters
  */
final case class SetChatDescription(chatId: ChatId, description: Option[String] = None)

object SetChatDescription {

  implicit val method: Method[SetChatDescription, Boolean] =
    new Method[SetChatDescription, Boolean] {

      def name: String = "setChatDescription"

      def encoder: Encoder[SetChatDescription] = deriveEncoder[SetChatDescription].snakeCase

      def decoder: Decoder[Boolean] = Decoder.decodeBoolean

      def uploads(request: SetChatDescription): List[(String, InputFile)] = Nil
    }
}
