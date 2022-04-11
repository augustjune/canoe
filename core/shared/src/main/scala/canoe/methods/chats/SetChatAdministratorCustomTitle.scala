package canoe.methods.chats

import canoe.marshalling.codecs._
import canoe.methods.Method
import canoe.models.{ChatId, InputFile}
import io.circe.{Decoder, Encoder}
import io.circe.generic.semiauto

final case class SetChatAdministratorCustomTitle(chatId: ChatId, userId: Long, customTitle: String)

object SetChatAdministratorCustomTitle {

  implicit val method: Method[SetChatAdministratorCustomTitle, Boolean] =
    new Method[SetChatAdministratorCustomTitle, Boolean] {

      def name: String = "setChatAdministratorCustomTitle"

      def encoder: Encoder[SetChatAdministratorCustomTitle] =
        semiauto.deriveEncoder[SetChatAdministratorCustomTitle].snakeCase

      def decoder: Decoder[Boolean] = Decoder.decodeBoolean

      def attachments(request: SetChatAdministratorCustomTitle): List[(String, InputFile)] = Nil
    }
}
