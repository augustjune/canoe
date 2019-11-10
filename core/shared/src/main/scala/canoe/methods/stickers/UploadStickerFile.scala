package canoe.methods.stickers

import canoe.marshalling.codecs._
import canoe.methods.Method
import canoe.models.{File, InputFile}
import io.circe.generic.semiauto._
import io.circe.{Decoder, Encoder}

/**
  * Use this method to upload a .png file with a sticker for later use in createNewStickerSet
  * and addStickerToSet methods (can be used multiple times).
  * Returns the uploaded File on success.
  *
  * @param userId      Integer User identifier of sticker file owner
  * @param pngSticker  InputFile Png image with the sticker, must be up to 512 kilobytes in size,
  *                    dimensions must not exceed 512px, and either width or height must be exactly 512px.
  *                    [[https://core.telegram.org/bots/api#sending-files More info on Sending Files]]
  */
final case class UploadStickerFile(userId: Int, pngSticker: InputFile)

object UploadStickerFile {
  import io.circe.generic.auto._

  implicit val method: Method[UploadStickerFile, File] =
    new Method[UploadStickerFile, File] {

      def name: String = "uploadStickerFile"

      def encoder: Encoder[UploadStickerFile] = deriveEncoder[UploadStickerFile].snakeCase

      def decoder: Decoder[File] = deriveDecoder[File]

      def attachments(request: UploadStickerFile): List[(String, InputFile)] =
        List("png_sticker" -> request.pngSticker)
    }
}
