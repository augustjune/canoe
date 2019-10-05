package canoe.methods.chats

import canoe.marshalling.codecs._
import canoe.methods.Method
import canoe.models.{ChatId, InputFile}
import io.circe.generic.semiauto._
import io.circe.{Decoder, Encoder}

/**
  * Use this method to delete a chat photo.
  * Photos can't be changed for private chats.
  *
  * The bot must be an administrator in the chat for this to work and
  * must have the appropriate admin rights.
  *
  * Returns True on success.
  *
  * @param chatId Unique identifier for the target chat or username of the target channel
  *               (in the format @channelusername)
  */
final case class DeleteChatPhoto(chatId: ChatId)

object DeleteChatPhoto {

  implicit val method: Method[DeleteChatPhoto, Boolean] =
    new Method[DeleteChatPhoto, Boolean] {

      def name: String = "deleteChatPhoto"

      def encoder: Encoder[DeleteChatPhoto] = deriveEncoder[DeleteChatPhoto].snakeCase

      def decoder: Decoder[Boolean] = Decoder.decodeBoolean

      def uploads(request: DeleteChatPhoto): List[(String, InputFile)] = Nil
    }
}
