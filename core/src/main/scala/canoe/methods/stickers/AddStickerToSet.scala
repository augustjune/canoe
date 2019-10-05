package canoe.methods.stickers

import canoe.marshalling.codecs._
import canoe.methods.Method
import canoe.models.{InputFile, MaskPosition}
import io.circe.generic.semiauto.deriveEncoder
import io.circe.{Decoder, Encoder}

/**
  * Use this method to add a new sticker to a set created by the bot.
  * Returns True on success.
  *
  * @param userId       User identifier of sticker set owner
  * @param name         Sticker set name
  * @param pngSticker   Png image with the sticker, must be up to 512 kilobytes in size, dimensions must not exceed 512px, and either width or height must be exactly 512px. Pass a file_id as a String to send a file that already exists on the Telegram servers, pass an HTTP URL as a String for Telegram to get a file from the Internet, or upload a new one using multipart/form-data. More info on Sending Files »
  * @param emojis       One or more emoji corresponding to the sticker
  * @param maskPosition Optional Position where the mask should be placed on faces
  */
final case class AddStickerToSet(userId: Int,
                                 name: String,
                                 pngSticker: InputFile,
                                 emojis: String,
                                 maskPosition: Option[MaskPosition] = None)

object AddStickerToSet {
  import io.circe.generic.auto._

  implicit val method: Method[AddStickerToSet, Boolean] =
    new Method[AddStickerToSet, Boolean] {

      def name: String = "addStickerToSet"

      def encoder: Encoder[AddStickerToSet] = deriveEncoder[AddStickerToSet].snakeCase

      def decoder: Decoder[Boolean] = Decoder.decodeBoolean

      def uploads(request: AddStickerToSet): List[(String, InputFile)] =
        List("png_sticker" -> request.pngSticker)
    }
}
