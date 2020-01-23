package canoe.models

/** This object represents a video file.
  *
  * @param fileId       Unique identifier for this file
  * @param fileUniqueId Unique identifier for this file, which is supposed to be the same over time and for different bots.
  *                     Can't be used to download or reuse the file.
  * @param width        Video width as defined by sender
  * @param height       Video height as defined by sender
  * @param duration     Duration of the video in seconds as defined by sender
  * @param thumb        Video thumbnail
  * @param mimeType     Mime type of a file as defined by sender
  * @param fileSize     File size
  */
final case class Video(fileId: String,
                       fileUniqueId: String,
                       width: Int,
                       height: Int,
                       duration: Int,
                       thumb: Option[PhotoSize],
                       mimeType: Option[String],
                       fileSize: Option[Int])
