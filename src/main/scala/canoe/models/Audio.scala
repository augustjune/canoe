package canoe.models

/** This object represents an audio file (voice note).
  *
  * @param fileId     Unique identifier for this file
  * @param duration   Duration of the audio in seconds as defined by sender
  * @param performer  Optional Performer of the audio as defined by sender or by audio tags
  * @param title      Optional Title of the audio as defined by sender or by audio tags
  * @param mimeType   Optional MIME type of the file as defined by sender
  * @param fileSize   Optional File size
  * @param thumb      PhotoSize Optional. Thumbnail of the album cover to which the music file belongs
  */
case class Audio(
  fileId: String,
  duration: Int,
  performer: Option[String] = None,
  title: Option[String] = None,
  mimeType: Option[String] = None,
  fileSize: Option[Int] = None,
  thumb: Option[PhotoSize]
)
