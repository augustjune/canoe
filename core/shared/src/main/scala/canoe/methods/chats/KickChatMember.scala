package canoe.methods.chats

import canoe.marshalling.codecs._
import canoe.methods.Method
import canoe.models.{ChatId, InputFile}
import io.circe.generic.semiauto.deriveEncoder
import io.circe.{Decoder, Encoder}

/**
  * Use this method to kick a user from a group, a supergroup or a channel.
  * In the case of supergroups and channels, the user will not be able to return to the group on their own
  * using invite links, etc., unless unbanned first.
  *
  * The bot must be an administrator in the chat for this to work and must have the appropriate admin rights.
  *
  * Returns True on success.
  *
  * @param chatId    Unique identifier for the target chat or username of the target channel
  *                  (in the format @channelusername)
  * @param userId    Unique identifier of the target user
  * @param untilDate Date when the user will be unbanned, unix time.
  *                  If user is banned for more than 366 days or less than 30 seconds from the current time
  *                  they are considered to be banned forever
  */
final case class KickChatMember(chatId: ChatId, userId: Int, untilDate: Option[Int] = None)

object KickChatMember {

  implicit val method: Method[KickChatMember, Boolean] =
    new Method[KickChatMember, Boolean] {

      def name: String = "kickChatMember"

      def encoder: Encoder[KickChatMember] = deriveEncoder[KickChatMember].snakeCase

      def decoder: Decoder[Boolean] = Decoder.decodeBoolean

      def attachments(request: KickChatMember): List[(String, InputFile)] = Nil
    }
}
