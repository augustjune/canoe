package canoe.methods

import canoe.models.InputFile
import io.circe.{Decoder, Encoder}

trait Method[Req, Resp] {

  def name: String

  def encoder: Encoder[Req]

  def decoder: Decoder[Resp]

  def uploads(request: Req): List[(String, InputFile)]
}
