package canoe.methods.chats

import canoe.marshalling.codecs._
import canoe.methods.Method
import canoe.models.{ChatId, InputFile}
import io.circe.generic.semiauto.deriveEncoder
import io.circe.{Decoder, Encoder}

/**
  * Use this method to restrict a user in a supergroup.
  * The bot must be an administrator in the supergroup for this to work and must have the appropriate admin rights.
  * Pass True for all boolean parameters to lift restrictions from a user.
  * Returns True on success.
  *
  * @param chatId                Integer or String	Unique identifier for the target chat or username of the target supergroup (in the format @supergroupusername)
  * @param userId                Integer	Yes	Unique identifier of the target user
  * @param untilDate             Integer Optional Date when restrictions will be lifted for the user, unix time.
  *                              If user is restricted for more than 366 days or less than 30 seconds from the current time, they are considered to be restricted forever
  * @param canSendMessages       Boolean	Optional Pass True, if the user can send text messages, contacts, locations and venues
  * @param canSendMediaMessages  Boolean	Optional Pass True, if the user can send audios, documents, photos, videos, video notes and voice notes, implies can_send_messages
  * @param canSendOtherMessages  Boolean	Optional Pass True, if the user can send animations, games, stickers and use inline bots, implies can_send_media_messages
  * @param canAddWebPagePreviews Boolean Optional Pass True, if the user may add web page previews to their messages, implies can_send_media_messages
  */
case class RestrictChatMember(chatId: ChatId,
                              userId: Int,
                              untilDate: Option[Int] = None,
                              canSendMessages: Option[Boolean] = None,
                              canSendMediaMessages: Option[Boolean] = None,
                              canSendOtherMessages: Option[Boolean] = None,
                              canAddWebPagePreviews: Option[Boolean] = None
                             )

object RestrictChatMember {

  implicit val method: Method[RestrictChatMember, Boolean] =
    new Method[RestrictChatMember, Boolean] {

      def name: String = "restrictChatMember"

      def encoder: Encoder[RestrictChatMember] = deriveEncoder[RestrictChatMember].snakeCase

      def decoder: Decoder[Boolean] = Decoder.decodeBoolean

      def uploads(request: RestrictChatMember): List[(String, InputFile)] = Nil
    }
}
