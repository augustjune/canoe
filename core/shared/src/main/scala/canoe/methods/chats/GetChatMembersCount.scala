package canoe.methods.chats

import canoe.marshalling.codecs._
import canoe.methods.Method
import canoe.models.{ChatId, InputFile}
import io.circe.generic.semiauto
import io.circe.{Decoder, Encoder}

/**
  * Use this method to get the number of members in a chat.
  *
  * Returns Int on success.
  *
  * @param chatId Unique identifier for the target chat or username of the target channel
  *               (in the format @channelusername)
  */
final case class GetChatMembersCount(chatId: ChatId)

object GetChatMembersCount {

  implicit val method: Method[GetChatMembersCount, Int] =
    new Method[GetChatMembersCount, Int] {

      def name: String = "getChatMembersCount"

      def encoder: Encoder[GetChatMembersCount] = semiauto.deriveEncoder[GetChatMembersCount].snakeCase

      def decoder: Decoder[Int] = Decoder.decodeInt

      def attachments(request: GetChatMembersCount): List[(String, InputFile)] = Nil
    }
}
