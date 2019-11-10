package canoe.methods.users

import canoe.methods.Method
import canoe.models.{InputFile, User}
import io.circe.generic.semiauto._
import io.circe.{Decoder, Encoder, Json}

/**
  * A simple method for testing your bot's auth token. Requires no parameters.
  * Returns basic information about the bot in form of a User object.
  */
case object GetMe {

  implicit val method: Method[GetMe.type, User] =
    new Method[GetMe.type, User] {

      def name: String = "getMe"

      def encoder: Encoder[GetMe.type] = Encoder.instance(_ => Json.Null)

      def decoder: Decoder[User] = deriveDecoder[User]

      def attachments(request: GetMe.type): List[(String, InputFile)] = Nil
    }
}
