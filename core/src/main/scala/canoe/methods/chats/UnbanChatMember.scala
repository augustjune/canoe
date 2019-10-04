package canoe.methods.chats

import canoe.marshalling.codecs._
import canoe.methods.Method
import canoe.models.{ChatId, InputFile}
import io.circe.generic.semiauto.deriveEncoder
import io.circe.{Decoder, Encoder}

/**
  * Use this method to unban a previously kicked user in a supergroup.
  * The user will not return to the group automatically, but will be able to join via link, etc.
  *
  * The bot must be an administrator in the group for this to work.
  * Returns True on success.
  *
  * @param chatId Unique identifier for the target chat or username of the target channel
  *               (in the format @channelusername)
  * @param userId Unique identifier of the target user
  */
case class UnbanChatMember(chatId: ChatId, userId: Int)

object UnbanChatMember {

  implicit val method: Method[UnbanChatMember, Boolean] =
    new Method[UnbanChatMember, Boolean] {

      def name: String = "unbanChatMember"

      def encoder: Encoder[UnbanChatMember] = deriveEncoder[UnbanChatMember].snakeCase

      def decoder: Decoder[Boolean] = Decoder.decodeBoolean

      def uploads(request: UnbanChatMember): List[(String, InputFile)] = Nil
    }
}
