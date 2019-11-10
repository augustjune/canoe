package canoe.methods

import java.io.Serializable

import canoe.models.InputFile
import io.circe.{Decoder, Encoder}

trait Method[Req, Resp] extends Serializable {

  def name: String

  def encoder: Encoder[Req]

  def decoder: Decoder[Resp]

  def attachments(request: Req): List[(String, InputFile)]
}
