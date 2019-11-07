package canoe.models

/**
  * Represents an animation file (GIF or H.264/MPEG-4 AVC video without sound).
  *
  * @param fileId   Unique identifier for this file
  * @param width    Video width as defined by sender
  * @param height   Video height as defined by sender
  * @param duration Duration of the video in seconds as defined by sender
  * @param thumb    Animation thumbnail as defined by sender
  * @param fileName Original animation filename as defined by sender
  * @param mimeType Mime type of a file as defined by sender
  * @param fileSize File size
  */
final case class Animation(fileId: String,
                           width: Int,
                           height: Int,
                           duration: Int,
                           thumb: Option[PhotoSize],
                           fileName: Option[String],
                           mimeType: Option[String],
                           fileSize: Option[Int])
