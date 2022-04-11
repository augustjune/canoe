package canoe.methods.chats

import canoe.marshalling.codecs._
import canoe.methods.Method
import canoe.models.{ChatId, InputFile}
import io.circe.generic.semiauto
import io.circe.{Decoder, Encoder}

/**
  * Use this method to change the title of a chat.
  * Titles can't be changed for private chats.
  *
  * The bot must be an administrator in the chat for this to work and
  * must have the appropriate admin rights.
  *
  * Returns True on success.
  *
  * @param chatId Unique identifier for the target chat or username of the target channel
  *               (in the format @channelusername)
  * @param title  New chat title, 1-255 characters
  */
final case class SetChatTitle(chatId: ChatId, title: String)

object SetChatTitle {

  implicit val method: Method[SetChatTitle, Boolean] =
    new Method[SetChatTitle, Boolean] {

      def name: String = "setChatTitle"

      def encoder: Encoder[SetChatTitle] = semiauto.deriveEncoder[SetChatTitle].snakeCase

      def decoder: Decoder[Boolean] = Decoder.decodeBoolean

      def attachments(request: SetChatTitle): List[(String, InputFile)] = Nil
    }
}
