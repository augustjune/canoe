package canoe.methods.chats

import canoe.marshalling.codecs._
import canoe.methods.Method
import canoe.models.{ChatId, InputFile}
import io.circe.generic.semiauto.deriveEncoder
import io.circe.{Decoder, Encoder}

/**
  * Use this method to set a new profile photo for the chat.
  * Photos can't be changed for private chats.
  *
  * The bot must be an administrator in the chat for this to work and
  * must have the appropriate admin rights.
  *
  * Returns True on success.
  *
  * @param chatId Unique identifier for the target chat or username of the target channel
  *               (in the format @channelusername)
  * @param photo  New chat photo
  */
final case class SetChatPhoto(chatId: ChatId, photo: InputFile)

object SetChatPhoto {

  implicit val method: Method[SetChatPhoto, Boolean] =
    new Method[SetChatPhoto, Boolean] {

      def name: String = "setChatPhoto"

      def encoder: Encoder[SetChatPhoto] = deriveEncoder[SetChatPhoto].snakeCase

      def decoder: Decoder[Boolean] = Decoder.decodeBoolean

      def attachments(request: SetChatPhoto): List[(String, InputFile)] = List("photo" -> request.photo)
    }
}
