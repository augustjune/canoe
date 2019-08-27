package canoe.methods.chats

import canoe.marshalling.CirceEncoders
import canoe.methods.Method
import canoe.models.{ChatId, InputFile}
import io.circe.{Decoder, Encoder}

/**
  * Use this method to export an invite link to a supergroup or a channel.
  * The bot must be an administrator in the chat for this to work and must have the appropriate admin rights.
  * Returns exported invite link as String on success.
  *
  * @param chatId	Integer or String Unique identifier for the target chat or username of the target channel (in the format @channelusername)
  */
case class ExportChatInviteLink(chatId : ChatId)

object ExportChatInviteLink {

  implicit val method: Method[ExportChatInviteLink, String] =
    new Method[ExportChatInviteLink, String] {

      def name: String = "exportChatInviteLink"

      def encoder: Encoder[ExportChatInviteLink] = CirceEncoders.exportChatInviteLinkEncoder

      def decoder: Decoder[String] = Decoder.decodeString

      def uploads(request: ExportChatInviteLink): List[(String, InputFile)] = Nil
    }
}
