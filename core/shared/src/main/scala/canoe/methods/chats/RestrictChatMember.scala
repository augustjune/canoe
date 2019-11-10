package canoe.methods.chats

import canoe.marshalling.codecs._
import canoe.methods.Method
import canoe.models.{ChatId, ChatPermissions, InputFile}
import io.circe.generic.semiauto.deriveEncoder
import io.circe.{Decoder, Encoder}

/**
  * Use this method to restrict a user in a supergroup.
  * The bot must be an administrator in the supergroup for this to work and must have the appropriate admin rights.
  *
  * @param chatId      Unique identifier for the target chat or username of the target channel
  *                    (in the format @channelusername)
  * @param userId      Unique identifier of the target user
  * @param permissions New user permissions
  * @param untilDate   Date when the user will be unbanned, unix time.
  *                    If user is banned for more than 366 days or less than 30 seconds from the current time
  *                    they are considered to be banned forever
  */
final case class RestrictChatMember(chatId: ChatId,
                                    userId: Int,
                                    permissions: ChatPermissions,
                                    untilDate: Option[Int] = None)

object RestrictChatMember {
  import io.circe.generic.auto._

  implicit val method: Method[RestrictChatMember, Boolean] =
    new Method[RestrictChatMember, Boolean] {

      def name: String = "restrictChatMember"

      def encoder: Encoder[RestrictChatMember] = deriveEncoder[RestrictChatMember].snakeCase

      def decoder: Decoder[Boolean] = Decoder.decodeBoolean

      def attachments(request: RestrictChatMember): List[(String, InputFile)] = Nil
    }
}
