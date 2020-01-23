package canoe.models

/**
  * @param smallFileId        Unique file identifier of small (160x160) chat photo. It can be used only for photo download.
  * @param smallFileUniqueId  Unique file identifier of small (160x160) chat photo, which is supposed to be the same over time and for different bots. 
  *                           Can't be used to download or reuse the file.
  * @param bigFileId          Unique file identifier of big (640x640) chat photo. It can be used only for photo download.
  * @param bigFileUniqueId    Unique file identifier of big (640x640) chat photo, which is supposed to be the same over time and for different bots.
  *                           Can't be used to download or reuse the file.
  */
final case class ChatPhoto(smallFileId: String, smallFileUniqueId: String, bigFileId: String, bigFileUniqueId: String)
