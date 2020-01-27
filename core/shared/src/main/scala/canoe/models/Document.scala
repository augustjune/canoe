package canoe.models

/**
  * Represents a general file (as opposed to photos, voice messages and audio files).
  *
  * @param fileId       Unique identifier
  * @param fileUniqueId Unique identifier for this file, which is supposed to be the same over time and for different bots. 
  *                     Can't be used to download or reuse the file.
  * @param thumb        Document thumbnail as defined by sender
  * @param fileName     Original filename as defined by sender
  * @param mimeType     MIME type of the file as defined by sender
  * @param fileSize     File size
  */
final case class Document(fileId: String,
                          fileUniqueId: String,
                          thumb: Option[PhotoSize],
                          fileName: Option[String],
                          mimeType: Option[String],
                          fileSize: Option[Int])
