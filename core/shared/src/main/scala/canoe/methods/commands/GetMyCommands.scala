package canoe.methods.commands

import canoe.methods.Method
import canoe.models.{BotCommand, InputFile}
import io.circe.{Decoder, Encoder, Json}
import io.circe.generic.semiauto.deriveDecoder

case object GetMyCommands {

  implicit val method: Method[GetMyCommands.type, List[BotCommand]] =
    new Method[GetMyCommands.type, List[BotCommand]] {
      def name: String = "getMyCommands"

      def encoder: Encoder[GetMyCommands.type] = Encoder.instance(_ => Json.Null)

      def decoder: Decoder[List[BotCommand]] = deriveDecoder[List[BotCommand]]

      def attachments(request: GetMyCommands.type): List[(String, InputFile)] = Nil
    }
}
