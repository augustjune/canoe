package canoe.methods.stickers

import canoe.marshalling.CirceEncoders
import canoe.methods.{JsonRequest, Method}
import canoe.models.{InputFile, StickerSet}
import io.circe.{Decoder, Encoder}

/**
  * Use this method to get a sticker set.
  * On success, a StickerSet object is returned.
  *
  * @param name  String Name of the sticker set
  */
case class GetStickerSet(name: String) extends JsonRequest[StickerSet]

object GetStickerSet {

  implicit val method: Method[GetStickerSet, Boolean] =
    new Method[GetStickerSet, Boolean] {

      def name: String = "getStickerSet"

      def encoder: Encoder[GetStickerSet] = CirceEncoders.getStickerSetEncoder

      def decoder: Decoder[Boolean] = Decoder.decodeBoolean

      def uploads(request: GetStickerSet): List[(String, InputFile)] = Nil
    }
}
