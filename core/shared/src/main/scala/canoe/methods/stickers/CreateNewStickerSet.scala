package canoe.methods.stickers

import canoe.marshalling.codecs._
import canoe.methods.Method
import canoe.models.{InputFile, MaskPosition}
import io.circe.generic.semiauto
import io.circe.{Decoder, Encoder}
import cats.syntax.all._

/**
  * Use this method to create new sticker set owned by a user.
  * The bot will be able to edit the created sticker set.
  *
  * Returns True on success.
  *
  * Use methods in companion object in order to construct the value of this class.
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
  * @param tgsSticker    TGS animation with the sticker, uploaded using multipart/form-data.
  *                      See [[https://core.telegram.org/animated_stickers#technical-requirements technical requirements]].
  * @param emojis        One or more emoji corresponding to the sticker
  * @param containsMasks Pass True, if a set of mask stickers should be created
  * @param maskPosition  Position where the mask should be placed on faces
  */
final class CreateNewStickerSet private (val userId: Int,
                                         val name: String,
                                         val title: String,
                                         val pngSticker: Option[InputFile],
                                         val tgsSticker: Option[InputFile],
                                         val emojis: String,
                                         val containsMasks: Option[Boolean],
                                         val maskPosition: Option[MaskPosition]
)

object CreateNewStickerSet {

  /**
    * Static sticker sets can have up to 120 stickers.
    * Note: Animated stickers can be added to animated sticker sets and only to them.
    */
  def static(userId: Int,
             name: String,
             title: String,
             sticker: InputFile,
             emojis: String,
             containsMasks: Option[Boolean] = None,
             maskPosition: Option[MaskPosition] = None
  ): CreateNewStickerSet =
    new CreateNewStickerSet(userId, name, title, Some(sticker), None, emojis, containsMasks, maskPosition)

  /**
    * Animated sticker sets can have up to 50 stickers.
    */
  def animated(userId: Int,
               name: String,
               title: String,
               sticker: InputFile,
               emojis: String,
               containsMasks: Option[Boolean] = None,
               maskPosition: Option[MaskPosition] = None
  ): CreateNewStickerSet =
    new CreateNewStickerSet(userId, name, title, None, Some(sticker), emojis, containsMasks, maskPosition)

  import io.circe.generic.auto._

  implicit val method: Method[CreateNewStickerSet, Boolean] =
    new Method[CreateNewStickerSet, Boolean] {

      def name: String = "createNewStickerSet"

      def encoder: Encoder[CreateNewStickerSet] = semiauto.deriveEncoder[CreateNewStickerSet].snakeCase

      def decoder: Decoder[Boolean] = Decoder.decodeBoolean

      def attachments(request: CreateNewStickerSet): List[(String, InputFile)] =
        List(request.pngSticker.tupleLeft("png_sticker"), request.tgsSticker.tupleLeft("tgs_sticker")).flatten
    }
}
