package canoe.methods.chats

import canoe.marshalling.CirceEncoders
import canoe.methods.{JsonRequest, Method}
import canoe.models.{ChatId, InputFile}
import io.circe.{Decoder, Encoder}

/** Use this method to unban a previously kicked user in a supergroup.
  * The user will not return to the group automatically, but will be able to join via link, etc.
  * The bot must be an administrator in the group for this to work. Returns True on success.
  *
  * @param chatId Integer or String Unique identifier for the target group or username of the target supergroup (in the format @supergroupusername)
  * @param userId Integer Unique identifier of the target user
  */
case class UnbanChatMember(chatId: ChatId, userId: Int) extends JsonRequest[Boolean]

object UnbanChatMember {

  implicit val method: Method[UnbanChatMember, Boolean] =
    new Method[UnbanChatMember, Boolean] {

      def name: String = "unbanChatMember"

      def encoder: Encoder[UnbanChatMember] = CirceEncoders.unbanChatMemberEncoder

      def decoder: Decoder[Boolean] = Decoder.decodeBoolean

      def uploads(request: UnbanChatMember): List[(String, InputFile)] = Nil
    }
}
