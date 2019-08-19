package canoe.methods.stickers

import canoe.methods.JsonRequest
import canoe.models.StickerSet

/**
  * Use this method to get a sticker set.
  * On success, a StickerSet object is returned.
  *
  * @param name  String Name of the sticker set
  */
case class GetStickerSet(name: String) extends JsonRequest[StickerSet]
