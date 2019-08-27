package canoe.methods.stickers

import canoe.marshalling.CirceEncoders
import canoe.methods.Method
import canoe.models.InputFile
import io.circe.{Decoder, Encoder}

/**
  * Use this method to delete a sticker from a set created by the bot.
  * Returns True on success.
  *
  * @param sticker String	File identifier of the sticker
  */
case class DeleteStickerFromSet(sticker: String)

object DeleteStickerFromSet {

  implicit val method: Method[DeleteStickerFromSet, Boolean] =
    new Method[DeleteStickerFromSet, Boolean] {

      def name: String = "deleteStickerFromSet"

      def encoder: Encoder[DeleteStickerFromSet] = CirceEncoders.deleteStickerFromSetEncoder

      def decoder: Decoder[Boolean] = Decoder.decodeBoolean

      def uploads(request: DeleteStickerFromSet): List[(String, InputFile)] = Nil
    }
}
