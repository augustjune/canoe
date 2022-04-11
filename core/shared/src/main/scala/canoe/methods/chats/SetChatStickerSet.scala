package canoe.methods.chats

import canoe.marshalling.codecs._
import canoe.methods.Method
import canoe.models.{ChatId, InputFile}
import io.circe.generic.semiauto
import io.circe.{Decoder, Encoder}

/**
  * Use this method to set a new group sticker set for a supergroup.
  *
  * The bot must be an administrator in the chat for this to work and
  * must have the appropriate admin rights.
  * Use the field can_set_sticker_set optionally returned in getChat requests to check
  * if the bot can use this method.
  *
  * Returns True on success.
  *
  * @param chatId         Unique identifier for the target chat or username of the target channel
  *                       (in the format @channelusername)
  * @param stickerSetName Name of the sticker set to be set as the group sticker set
  */
final case class SetChatStickerSet(chatId: ChatId, stickerSetName: String)

object SetChatStickerSet {

  implicit val method: Method[SetChatStickerSet, Boolean] =
    new Method[SetChatStickerSet, Boolean] {

      def name: String = "setChatStickerSet"

      def encoder: Encoder[SetChatStickerSet] = semiauto.deriveEncoder[SetChatStickerSet].snakeCase

      def decoder: Decoder[Boolean] = Decoder.decodeBoolean

      def attachments(request: SetChatStickerSet): List[(String, InputFile)] = Nil
    }
}
