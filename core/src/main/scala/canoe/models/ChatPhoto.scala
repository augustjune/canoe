package canoe.models

/**
  * @param smallFileId  Unique file identifier of small (160x160) chat photo. It can be used only for photo download.
  * @param bigFileId    Unique file identifier of big (640x640) chat photo. It can be used only for photo download.
  */
case class ChatPhoto(smallFileId: String, bigFileId: String)
