package canoe.methods.chats

import canoe.marshalling.codecs._
import canoe.methods.Method
import canoe.models.{ChatId, ChatPermissions, InputFile}
import io.circe.generic.semiauto.deriveEncoder
import io.circe.{Decoder, Encoder}

/**
  * Use this method to set default chat permissions for all members.
  * The bot must be an administrator in the group or a supergroup for this to work
  * and must have the can_restrict_members admin rights.
  *
  * @param chatId      Unique identifier for the target chat or username of the target channel
  *                    (in the format @channelusername)
  * @param permissions New default chat permissions
  */
case class SetChatPermissions(chatId: ChatId, permissions: ChatPermissions)

object SetChatPermissions {
  import io.circe.generic.auto._

  implicit val method: Method[SetChatPermissions, Boolean] =
    new Method[SetChatPermissions, Boolean] {

      def name: String = "setChatPermissions"

      def encoder: Encoder[SetChatPermissions] = deriveEncoder[SetChatPermissions].snakeCase

      def decoder: Decoder[Boolean] = Decoder.decodeBoolean

      def uploads(request: SetChatPermissions): List[(String, InputFile)] = Nil
    }
}
