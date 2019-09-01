package canoe.methods.chats

import canoe.marshalling.codecs._
import canoe.methods.Method
import canoe.models.{ChatId, ChatPermissions, InputFile}
import io.circe.generic.semiauto.deriveEncoder
import io.circe.{Decoder, Encoder}

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
