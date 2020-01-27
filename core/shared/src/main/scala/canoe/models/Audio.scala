package canoe.models

/**
  * Represents an audio file to be treated as music by the Telegram clients.
  *
  * @param fileId       Unique identifier
  * @param fileUniqueId Unique identifier for this file, which is supposed to be the same over time and for different bots.
  *                     Can't be used to download or reuse the file.
  * @param duration     Duration of the audio in seconds as defined by sender
  * @param performer    Performer of the audio as defined by sender or by audio tags
  * @param title        Title of the audio as defined by sender or by audio tags
  * @param mimeType     MIME type of the file as defined by sender
  * @param fileSize     File size
  * @param thumb        Thumbnail of the album cover to which the music file belongs
  */
final case class Audio(fileId: String,
                       fileUniqueId: String,
                       duration: Int,
                       performer: Option[String],
                       title: Option[String],
                       mimeType: Option[String],
                       fileSize: Option[Int],
                       thumb: Option[PhotoSize])
