package canoe.methods.stickers

import canoe.marshalling.codecs._
import canoe.methods.Method
import canoe.models.{InputFile, MaskPosition}
import io.circe.generic.semiauto
import io.circe.{Decoder, Encoder}
import cats.syntax.all._

/** Use this method to add a new sticker to a set created by the bot.
  * Returns True on success.
  *
  * Use methods in companion object in order to construct the value of this class.
  *
  * @param userId       User identifier of sticker set owner
  * @param name         Sticker set name
  * @param pngSticker   Png image with the sticker, must be up to 512 kilobytes in size, dimensions must not exceed 512px, and either width or height must be exactly 512px. Pass a file_id as a String to send a file that already exists on the Telegram servers, pass an HTTP URL as a String for Telegram to get a file from the Internet, or upload a new one using multipart/form-data. More info on Sending Files »
  * @param tgsSticker   TGS animation with the sticker, uploaded using multipart/form-data.
  *                     See [[https://core.telegram.org/animated_stickers#technical-requirements technical requirements]].
  * @param emojis       One or more emoji corresponding to the sticker
  * @param maskPosition Position where the mask should be placed on faces
  */
final case class AddStickerToSet private (userId: Long,
                                          name: String,
                                          pngSticker: Option[InputFile],
                                          tgsSticker: Option[InputFile],
                                          emojis: String,
                                          maskPosition: Option[MaskPosition]
)

object AddStickerToSet {

  /** @param sticker PNG image with the sticker.
    */
  def static(userId: Long,
             name: String,
             sticker: InputFile,
             emojis: String,
             maskPosition: Option[MaskPosition]
  ): AddStickerToSet =
    new AddStickerToSet(userId, name, Some(sticker), None, emojis, maskPosition)

  /** @param sticker TGS animation with the sticker.
    * Animated stickers can be added to animated sticker sets and only to them.
    */
  def animated(userId: Long,
               name: String,
               sticker: InputFile,
               emojis: String,
               maskPosition: Option[MaskPosition]
  ): AddStickerToSet =
    new AddStickerToSet(userId, name, None, Some(sticker), emojis, maskPosition)

  import io.circe.generic.auto._

  implicit val method: Method[AddStickerToSet, Boolean] =
    new Method[AddStickerToSet, Boolean] {

      def name: String = "addStickerToSet"

      def encoder: Encoder[AddStickerToSet] = semiauto.deriveEncoder[AddStickerToSet].snakeCase

      def decoder: Decoder[Boolean] = Decoder.decodeBoolean

      def attachments(request: AddStickerToSet): List[(String, InputFile)] =
        List(request.pngSticker.tupleLeft("png_sticker"), request.tgsSticker.tupleLeft("tgs_sticker")).flatten
    }
}
