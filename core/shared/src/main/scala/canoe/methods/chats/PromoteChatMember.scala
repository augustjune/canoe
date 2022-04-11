package canoe.methods.chats

import canoe.marshalling.codecs._
import canoe.methods.Method
import canoe.models.{ChatId, InputFile}
import io.circe.generic.semiauto
import io.circe.{Decoder, Encoder}

/**
  * Use this method to promote or demote a user in a supergroup or a channel.
  * The bot must be an administrator in the chat for this to work and must have the appropriate admin rights.
  *
  * Pass False for all boolean parameters to demote a user.
  * Returns True on success.
  *
  * @param chatId             Integer or String Unique identifier for the target chat or username of the target channel
  *                           (in the format @channelusername)
  * @param userId             Unique identifier of the target user
  * @param canChangeInfo      Pass True, if the administrator can change chat title, photo and other settings
  * @param canPostMessages    Pass True, if the administrator can create channel posts, channels only
  * @param canEditMessages    Pass True, if the administrator can edit messages of other users, channels only
  * @param canDeleteMessages  Pass True, if the administrator can delete messages of other users
  * @param canInviteUsers     Pass True, if the administrator can invite new users to the chat
  * @param canRestrictMembers Pass True, if the administrator can restrict, ban or unban chat members
  * @param canPinMessages     Pass True, if the administrator can pin messages, supergroups only
  * @param canPromoteMembers  Pass True, if the administrator can add new administrators with a subset
  *                           of his own privileges or demote administrators that he has promoted,
  *                           directly or indirectly (promoted by administrators that were appointed by him)
  */
final case class PromoteChatMember(chatId: ChatId,
                                   userId: Long,
                                   canChangeInfo: Option[Boolean] = None,
                                   canPostMessages: Option[Boolean] = None,
                                   canEditMessages: Option[Boolean] = None,
                                   canDeleteMessages: Option[Boolean] = None,
                                   canInviteUsers: Option[Boolean] = None,
                                   canRestrictMembers: Option[Boolean] = None,
                                   canPinMessages: Option[Boolean] = None,
                                   canPromoteMembers: Option[Boolean] = None)

object PromoteChatMember {

  implicit val method: Method[PromoteChatMember, Boolean] =
    new Method[PromoteChatMember, Boolean] {

      def name: String = "promoteChatMember"

      def encoder: Encoder[PromoteChatMember] = semiauto.deriveEncoder[PromoteChatMember].snakeCase

      def decoder: Decoder[Boolean] = Decoder.decodeBoolean

      def attachments(request: PromoteChatMember): List[(String, InputFile)] = Nil
    }
}
