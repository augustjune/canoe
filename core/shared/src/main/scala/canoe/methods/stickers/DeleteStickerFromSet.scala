package canoe.methods.stickers

import canoe.marshalling.codecs._
import canoe.methods.Method
import canoe.models.InputFile
import io.circe.generic.semiauto
import io.circe.{Decoder, Encoder}

/**
  * Use this method to delete a sticker from a set created by the bot.
  *
  * Returns True on success.
  *
  * @param sticker   Sticker's fileId
  */
final case class DeleteStickerFromSet(sticker: String)

object DeleteStickerFromSet {

  implicit val method: Method[DeleteStickerFromSet, Boolean] =
    new Method[DeleteStickerFromSet, Boolean] {

      def name: String = "deleteStickerFromSet"

      def encoder: Encoder[DeleteStickerFromSet] = semiauto.deriveEncoder[DeleteStickerFromSet].snakeCase

      def decoder: Decoder[Boolean] = Decoder.decodeBoolean

      def attachments(request: DeleteStickerFromSet): List[(String, InputFile)] = Nil
    }
}
