package canoe.models

/**
  * @param name           Sticker set name
  * @param title          Sticker set title
  * @param containsMasks  True, if the sticker set contains masks
  * @param stickers       List of all set stickers
  */
final case class StickerSet(name: String,
                            title: String,
                            isAnimated: Boolean,
                            containsMasks: Boolean,
                            stickers: List[Sticker],
                            thumb: Option[PhotoSize])
