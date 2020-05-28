package canoe.methods.commands

import canoe.marshalling.codecs._
import canoe.methods.Method
import canoe.models.{BotCommand, InputFile}
import io.circe.generic.semiauto.deriveEncoder
import io.circe.{Decoder, Encoder}

/**
  * Use this method to change the list of the bot's commands.
  *
  * Returns True on success.
  *
  * @param commands At most 100 commands can be specified.
  */
final case class SetMyCommands(commands: List[BotCommand])

object SetMyCommands {
  import io.circe.generic.auto._

  implicit val method: Method[SetMyCommands, Boolean] =
    new Method[SetMyCommands, Boolean] {
      def name: String = "setMyCommands"

      def encoder: Encoder[SetMyCommands] = deriveEncoder[SetMyCommands].snakeCase

      def decoder: Decoder[Boolean] = Decoder.decodeBoolean

      def attachments(request: SetMyCommands): List[(String, InputFile)] = Nil
    }
}
