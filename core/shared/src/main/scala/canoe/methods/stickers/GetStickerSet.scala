package canoe.methods.stickers

import canoe.marshalling.codecs._
import canoe.methods.Method
import canoe.models.InputFile
import io.circe.generic.semiauto.deriveEncoder
import io.circe.{Decoder, Encoder}

/**
  * Use this method to get a sticker set.
  *
  * On success, a StickerSet object is returned.
  *
  * @param name Name of the sticker set
  */
final case class GetStickerSet(name: String)

object GetStickerSet {

  implicit val method: Method[GetStickerSet, Boolean] =
    new Method[GetStickerSet, Boolean] {

      def name: String = "getStickerSet"

      def encoder: Encoder[GetStickerSet] = deriveEncoder[GetStickerSet].snakeCase

      def decoder: Decoder[Boolean] = Decoder.decodeBoolean

      def attachments(request: GetStickerSet): List[(String, InputFile)] = Nil
    }
}
