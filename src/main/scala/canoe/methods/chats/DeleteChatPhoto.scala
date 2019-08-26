package canoe.methods.chats

import canoe.marshalling.CirceEncoders
import canoe.methods.{JsonRequest, Method}
import canoe.models.{ChatId, InputFile}
import io.circe.{Decoder, Encoder}

/**
  * Use this method to delete a chat photo.
  * Photos can't be changed for private chats.
  * The bot must be an administrator in the chat for this to work and must have the appropriate admin rights.
  * Returns True on success.
  *
  * '''Note:'''
  * In regular groups (non-supergroups), this method will only work if the "All Members Are Admins" setting is off in the target group.
  *
  * @param chatId Integer or String Unique identifier for the target chat or username of the target channel (in the format @channelusername)
  */
case class DeleteChatPhoto(chatId: ChatId) extends JsonRequest[Boolean]

object DeleteChatPhoto {

  implicit val method: Method[DeleteChatPhoto, Boolean] =
    new Method[DeleteChatPhoto, Boolean] {

      def name: String = "deleteChatPhoto"

      def encoder: Encoder[DeleteChatPhoto] = CirceEncoders.deleteChatPhotoEncoder

      def decoder: Decoder[Boolean] = Decoder.decodeBoolean

      def uploads(request: DeleteChatPhoto): List[(String, InputFile)] = Nil
    }
}
