package canoe.methods.stickers

import canoe.marshalling.codecs._
import canoe.methods.Method
import canoe.models.{InputFile, MaskPosition}
import io.circe.generic.semiauto.deriveEncoder
import io.circe.{Decoder, Encoder}

/**
  * Use this method to create new sticker set owned by a user.
  * The bot will be able to edit the created sticker set.
  *
  * Returns True on success.
  *
  * @param userId        User identifier of created sticker set owner
  * @param name          Short name of sticker set, to be used in t.me/addstickers/ URLs (e.g., animals).
  *                      Can contain only english letters, digits and underscores.
  *                      Must begin with a letter.
  *                      Can't contain consecutive underscores and
  *                      must end in “_by_<bot username>”. <bot_username> is case insensitive.
  *                      1-64 characters.
  * @param title         Sticker set title, 1-64 characters
  * @param pngSticker    Sticker file (can be existing Telegram file or new one in .png format)
  * @param emojis        One or more emoji corresponding to the sticker
  * @param containsMasks Pass True, if a set of mask stickers should be created
  * @param maskPosition  Position where the mask should be placed on faces
  */
case class CreateNewStickerSet(userId: Int,
                               name: String,
                               title: String,
                               pngSticker: InputFile,
                               emojis: String,
                               containsMasks: Option[Boolean] = None,
                               maskPosition: Option[MaskPosition] = None)

object CreateNewStickerSet {
  import io.circe.generic.auto._

  implicit val method: Method[CreateNewStickerSet, Boolean] =
    new Method[CreateNewStickerSet, Boolean] {

      def name: String = "createNewStickerSet"

      def encoder: Encoder[CreateNewStickerSet] = deriveEncoder[CreateNewStickerSet].snakeCase

      def decoder: Decoder[Boolean] = Decoder.decodeBoolean

      def uploads(request: CreateNewStickerSet): List[(String, InputFile)] =
        List("png_sticker" -> request.pngSticker)
    }
}
