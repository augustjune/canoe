package canoe.models

/**
  * @param fileId       Unique identifier for this file
  * @param fileUniqueId Unique identifier for this file, which is supposed to be the same over time and for different bots.
  *                     Can't be used to download or reuse the file.
  * @param width        Sticker width
  * @param height       Sticker height
  * @param thumb        Sticker thumbnail in .webp or .jpg format
  * @param emoji        Emoji associated with the sticker
  * @param setName      Name of the sticker set to which the sticker belongs
  * @param maskPosition For mask stickers, the position where the mask should be placed
  * @param fileSize     File size
  */
final case class Sticker(fileId: String,
                         fileUniqueId: String,
                         width: Int,
                         height: Int,
                         isAnimated: Boolean,
                         thumb: Option[PhotoSize],
                         emoji: Option[String],
                         setName: Option[String],
                         maskPosition: Option[MaskPosition],
                         fileSize: Option[Int])
