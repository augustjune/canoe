package canoe.models

/**
  * Represents a file ready to be downloaded.
  *
  * The file can be downloaded via the link
  * https://api.telegram.org/file/bot<token>/<file_path>.
  *
  * It is guaranteed that the link will be valid for at least 1 hour.
  * When the link expires, a new one can be requested by using [[canoe.methods.files.GetFile]].
  * Maximum file size to download is 20 MB
  *
  * @param fileId       Unique identifier for this file
  * @param fileUniqueId Unique identifier for this file, which is supposed to be the same over time and for different bots.
  *                     Can't be used to download or reuse the file.
  * @param fileSize     File size, if known
  * @param filePath     File path. Use https://api.telegram.org/file/bot<token>/<file_path> to get the file.
  */
final case class File(fileId: String, fileUniqueId: String, fileSize: Option[Int], filePath: Option[String])
