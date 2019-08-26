package canoe.methods

import java.io.Serializable

import canoe.models.InputFile
import io.circe.{Decoder, Encoder}

trait Method[Req, Resp] extends Serializable { self =>

  def name: String

  def encoder: Encoder[Req]

  def decoder: Decoder[Resp]

  def uploads(request: Req): List[(String, InputFile)]

  def map[Resp2](f: Resp => Resp2): Method[Req, Resp2] =
    new Method[Req, Resp2] {
      def name: String = self.name

      def encoder: Encoder[Req] = self.encoder

      def decoder: Decoder[Resp2] = self.decoder.map(f)

      def uploads(request: Req): List[(String, InputFile)] = self.uploads(request)
    }

  def contramap[Req2](f: Req2 => Req): Method[Req2, Resp] =
    new Method[Req2, Resp] {
      def name: String = self.name

      def encoder: Encoder[Req2] = self.encoder.contramap(f)

      def decoder: Decoder[Resp] = self.decoder

      def uploads(request: Req2): List[(String, InputFile)] =
        self.uploads(f(request))
    }
}
