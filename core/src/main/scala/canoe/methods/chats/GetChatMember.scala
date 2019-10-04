package canoe.methods.chats

import canoe.marshalling.codecs._
import canoe.methods.Method
import canoe.models.{ChatId, ChatMember, InputFile}
import io.circe.generic.semiauto.deriveEncoder
import io.circe.{Decoder, Encoder}

/**
  * Use this method to get information about a member of a chat.
  *
  * Returns a ChatMember object on success.
  *
  * @param chatId Unique identifier for the target chat or username of the target channel
  *               (in the format @channelusername)
  * @param userId Unique identifier of the target user
  */
case class GetChatMember(chatId: ChatId, userId: Int)

object GetChatMember {

  implicit val method: Method[GetChatMember, ChatMember] =
    new Method[GetChatMember, ChatMember] {

      def name: String = "getChatMember"

      def encoder: Encoder[GetChatMember] = deriveEncoder[GetChatMember].snakeCase

      def decoder: Decoder[ChatMember] = ChatMember.chatMemberDecoder

      def uploads(request: GetChatMember): List[(String, InputFile)] = Nil
    }
}
