package canoe.methods.stickers

import canoe.methods.Method
import canoe.models.InputFile
import io.circe.{Decoder, Encoder}
import io.circe.generic.semiauto
import canoe.marshalling.codecs._

/**
  * Use this method to set the thumbnail of a sticker set.
  * Animated thumbnails can be set for animated sticker sets only.
  *
  * @param name   Sticker set name.
  * @param userId User identifier of the sticker set owner.
  * @param thumb  A PNG image with the thumbnail, must be up to 128 kilobytes in size and have width and height exactly 100px,
  *               or a TGS animation with the thumbnail up to 32 kilobytes in size;
  */
final case class SetStickerSetThumb(name: String, userId: Long, thumb: InputFile)

object SetStickerSetThumb {
  implicit val method: Method[SetStickerSetThumb, Boolean] =
    new Method[SetStickerSetThumb, Boolean] {
      def name: String = "setStickerSetThumb"

      def encoder: Encoder[SetStickerSetThumb] = semiauto.deriveEncoder[SetStickerSetThumb].snakeCase

      def decoder: Decoder[Boolean] = Decoder.decodeBoolean

      def attachments(request: SetStickerSetThumb): List[(String, InputFile)] =
        List("thumb" -> request.thumb)
    }
}
