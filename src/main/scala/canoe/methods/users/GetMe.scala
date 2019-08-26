package canoe.methods.users

import canoe.marshalling.{CirceDecoders, CirceEncoders}
import canoe.methods.{JsonRequest, Method}
import canoe.models.{InputFile, User}
import io.circe.{Decoder, Encoder}

/** A simple method for testing your bot's auth token. Requires no parameters.
  * Returns basic information about the bot in form of a User object.
  */
case object GetMe extends JsonRequest[User] {

  implicit val method: Method[GetMe.type, User] =
    new Method[GetMe.type, User] {

      def name: String = "getMe"

      def encoder: Encoder[GetMe.type] = CirceEncoders.getMeEncoder

      def decoder: Decoder[User] = CirceDecoders.userDecoder

      def uploads(request: GetMe.type): List[(String, InputFile)] = Nil
    }
}
