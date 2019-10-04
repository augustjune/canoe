package canoe.models

/**
  * Represents an audio file to be treated as music by the Telegram clients.
  *
  * @param fileId    Unique identifier
  * @param duration  Duration of the audio in seconds as defined by sender
  * @param performer Performer of the audio as defined by sender or by audio tags
  * @param title     Title of the audio as defined by sender or by audio tags
  * @param mimeType  MIME type of the file as defined by sender
  * @param fileSize  File size
  * @param thumb     Thumbnail of the album cover to which the music file belongs
  */
case class Audio(fileId: String,
                 duration: Int,
                 performer: Option[String],
                 title: Option[String],
                 mimeType: Option[String],
                 fileSize: Option[Int],
                 thumb: Option[PhotoSize])
