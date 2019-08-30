package canoe.models

import io.circe.{Encoder, Json}

/** This object represents the contents of a file to be uploaded.
  * Must be posted using multipart/form-data in the usual way that files are uploaded via the browser.
  *
  * Resending files without reuploading
  * There are two ways of sending a file (photo, sticker, audio etc.).
  *     If it's a new file, you can upload it using multipart/form-data.
  *     If the file is already on our servers, you don't need to reupload it: each file object has a file_id field, you can simply pass this file_id as a parameter instead.
  *
  * It is not possible to change the file type when resending by file_id. I.e. a video can't be sent as a photo, a photo can't be sent as a document, etc.
  * It is not possible to resend thumbnails.
  * Resending a photo by file_id will send all of its sizes.
  */
sealed trait InputFile

object InputFile {

  /**
    * File which should be uploaded
    */
  final case class Upload(filename: String, contents: Array[Byte]) extends InputFile

  /**
    * File existing in the telegram or on the web
    *
    * @param key telegram FileId or URL
    */
  final case class Existing(key: String) extends InputFile

  def fromUrl(url: String): InputFile = Existing(url)
  def fromFileId(fileId: String): InputFile = Existing(fileId)
  def fromBytes(name: String, bytes: Array[Byte]) = Upload(name, bytes)

  implicit def inputFileEncoder: Encoder[InputFile] = Encoder.instance[InputFile] {
    case InputFile.Upload(_, _)     => Json.Null
    case InputFile.Existing(handle) => Json.fromString(handle)
  }
}
