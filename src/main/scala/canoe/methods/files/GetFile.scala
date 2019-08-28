package canoe.methods.files

import canoe.marshalling.CirceDecoders
import canoe.marshalling.codecs._
import canoe.methods.Method
import canoe.models.{File, InputFile}
import io.circe.generic.semiauto.deriveEncoder
import io.circe.{Decoder, Encoder}

/** Use this method to get basic info about a file and prepare it for downloading.
  * For the moment, bots can download files of up to 20MB in size. On success, a File object is returned.
  * The file can then be downloaded via the link https://api.telegram.org/file/bot<token>/<file_path>,
  * where <file_path> is taken from the response.
  * It is guaranteed that the link will be valid for at least 1 hour.
  * When the link expires, a new one can be requested by calling getFile again.
  *
  * @param fileId String File identifier to get info about
  */
case class GetFile(fileId : String)

object GetFile {

  implicit val method: Method[GetFile, File] =
    new Method[GetFile, File] {

      def name: String = "getFile"

      def encoder: Encoder[GetFile] = deriveEncoder[GetFile].snakeCase

      def decoder: Decoder[File] = CirceDecoders.fileDecoder

      def uploads(request: GetFile): List[(String, InputFile)] = Nil
    }
}
