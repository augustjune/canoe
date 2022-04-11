package canoe.models

import canoe.models.ParseMode.ParseMode
import io.circe.Encoder
import io.circe.generic.auto._
import io.circe.syntax._

/**
  * Represents the content of a media message to be sent.
  */
sealed trait InputMedia extends Product {
  def files: List[(String, InputFile)] = List(`type` -> media)

  def media: InputFile

  def `type`: String
}

object InputMedia {
  /**
    * Encoder for telegram api.
    */
  implicit val encodeInputMedia: Encoder[InputMedia] = Encoder.instance {
    case foo@InputMediaPhoto(_,_,_,_)             => foo.asJson
    case foo@InputMediaVideo(_,_,_,_,_,_,_,_)     => foo.asJson
    case foo@InputMediaAnimation(_,_,_,_,_,_,_,_) => foo.asJson
    case foo@InputMediaAudio(_,_,_,_,_,_,_,_)     => foo.asJson
    case foo@InputMediaDocument(_,_,_,_,_)        => foo.asJson
  }
}

/**
  * Represents a photo to be sent.
  *
  * @param type      Type of the result, must be photo
  * @param media     Input media file
  * @param caption   Caption of the photo to be sent, 0-1024 characters
  * @param parseMode Parse mode of captured text
  *
  */
final case class InputMediaPhoto(media: InputFile,
                                 caption: Option[String] = None,
                                 parseMode: Option[ParseMode] = None,
                                 `type`: String = "photo")
    extends InputMedia

/**
  * Represents a video to be sent.
  *
  * @param type              Type of the result, must be video
  * @param media             Input media file
  * @param caption           Caption of the video to be sent, 0-1024 characters
  * @param width             Video width
  * @param height            Video height
  * @param duration          Video duration
  * @param parseMode         Parse mode of captured text
  * @param supportsStreaming Pass True, if the uploaded video is suitable for streaming
  */
final case class InputMediaVideo(media: InputFile,
                                 caption: Option[String] = None,
                                 width: Option[Int] = None,
                                 height: Option[Int] = None,
                                 duration: Option[Int] = None,
                                 parseMode: Option[ParseMode] = None,
                                 supportsStreaming: Option[Boolean] = None,
                                 `type`: String = "video")
    extends InputMedia

/**
  * Represents an animation file (GIF or H.264/MPEG-4 AVC video without sound) to be sent.
  *
  * @param type      Type of the result, must be animation
  * @param media     Input media file
  * @param thumb     Thumbnail of the file sent
  * @param caption   Caption of the animation to be sent, 0-1024 characters
  * @param parseMode Parse mode of captured text
  * @param width     Animation width
  * @param height    Animation height
  * @param duration  Animation duration
  */
final case class InputMediaAnimation(media: InputFile,
                                     thumb: Option[InputFile] = None,
                                     caption: Option[String] = None,
                                     parseMode: Option[ParseMode] = None,
                                     width: Option[Int] = None,
                                     height: Option[Int] = None,
                                     duration: Option[Int] = None,
                                     `type`: String = "animation")
    extends InputMedia

/**
  * Represents an audio file to be treated as music to be sent.
  *
  * @param type      Type of the result, must be audio
  * @param media     Input media file
  * @param thumb     Thumbnail of the file sent
  * @param caption   Caption of the audio to be sent, 0-1024 characters
  * @param parseMode Parse mode of input text
  * @param duration  Duration of the audio in seconds
  * @param performer Performer of the audio
  * @param title     Title of the audio
  */
final case class InputMediaAudio(media: InputFile,
                                 thumb: Option[InputFile] = None,
                                 caption: Option[String] = None,
                                 parseMode: Option[ParseMode] = None,
                                 duration: Option[Int] = None,
                                 performer: Option[String] = None,
                                 title: Option[String] = None,
                                 `type`: String = "audio")
    extends InputMedia

/**
  * Represents a general file to be sent.
  *
  * @param type      String Type of the result, must be document
  * @param media     Input media file
  * @param thumb     Thumbnail of the file sent
  * @param caption   Caption of the document to be sent, 0-1024 characters
  * @param parseMode Parse mode of input text
  */
final case class InputMediaDocument(media: InputFile,
                                    thumb: Option[InputFile] = None,
                                    caption: Option[String] = None,
                                    parseMode: Option[ParseMode] = None,
                                    `type`: String = "document")
    extends InputMedia
