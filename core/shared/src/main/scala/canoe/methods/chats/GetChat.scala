package canoe.methods.chats

import canoe.marshalling.codecs._
import canoe.methods.Method
import canoe.models.{ChatId, DetailedChat, InputFile}
import io.circe.generic.semiauto._
import io.circe.{Decoder, Encoder}

/**
  * Use this method to get up to date information about the chat (current name of the user
  * for one-on-one conversations, current username of a user, group or channel, etc.)
  *
  * Returns a DetailedChat object on success.
  *
  * @param chatId Unique identifier for the target chat or username of the target channel
  *               (in the format @channelusername)
  */
final case class GetChat(chatId: ChatId)

object GetChat {
  import io.circe.generic.auto._

  implicit val method: Method[GetChat, DetailedChat] =
    new Method[GetChat, DetailedChat] {

      def name: String = "getChat"

      def encoder: Encoder[GetChat] = deriveEncoder[GetChat].snakeCase

      def decoder: Decoder[DetailedChat] = deriveDecoder[DetailedChat]

      def attachments(request: GetChat): List[(String, InputFile)] = Nil
    }
}
