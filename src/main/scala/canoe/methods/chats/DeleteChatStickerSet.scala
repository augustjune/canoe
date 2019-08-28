package canoe.methods.chats

import canoe.marshalling.codecs._
import canoe.methods.Method
import canoe.models.{ChatId, InputFile}
import io.circe.generic.semiauto._
import io.circe.{Decoder, Encoder}

/**
  * Use this method to delete a group sticker set from a supergroup.
  * The bot must be an administrator in the chat for this to work and must have the appropriate admin rights.
  * Use the field can_set_sticker_set optionally returned in getChat requests to check if the bot can use this method.
  * Returns True on success.
 *
  * @param chatId Integer or String Yes Unique identifier for the target chat or username of the target supergroup (in the format @supergroupusername)
  */
case class DeleteChatStickerSet(chatId: ChatId)

object DeleteChatStickerSet {

  implicit val method: Method[DeleteChatStickerSet, Boolean] =
    new Method[DeleteChatStickerSet, Boolean] {

      def name: String = "deleteChatStickerSet"

      def encoder: Encoder[DeleteChatStickerSet] = deriveEncoder[DeleteChatStickerSet].snakeCase

      def decoder: Decoder[Boolean] = Decoder.decodeBoolean

      def uploads(request: DeleteChatStickerSet): List[(String, InputFile)] = Nil
    }
}
