package canoe.methods.stickers

import canoe.marshalling.codecs._
import canoe.methods.Method
import canoe.models.InputFile
import io.circe.generic.semiauto.deriveEncoder
import io.circe.{Decoder, Encoder}

/**
  * Use this method to move a sticker in a set created by the bot to a specific position.
  * Returns True on success.
  *
  * @param sticker   String File identifier of the sticker
  * @param position  Integer New sticker position in the set, zero-based
  */
case class SetStickerPositionInSet(sticker: String, position: Int)

object SetStickerPositionInSet {

  implicit val method: Method[SetStickerPositionInSet, Boolean] =
    new Method[SetStickerPositionInSet, Boolean] {

      def name: String = "setStickerPositionInSet"

      def encoder: Encoder[SetStickerPositionInSet] = deriveEncoder[SetStickerPositionInSet].snakeCase

      def decoder: Decoder[Boolean] = Decoder.decodeBoolean

      def uploads(request: SetStickerPositionInSet): List[(String, InputFile)] = Nil
    }
}
