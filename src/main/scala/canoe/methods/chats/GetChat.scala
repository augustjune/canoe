package canoe.methods.chats

import canoe.marshalling.{CirceDecoders, CirceEncoders}
import canoe.methods.{JsonRequest, Method}
import canoe.models.{ChatId, DetailedChat, InputFile}
import io.circe.{Decoder, Encoder}

/** Use this method to get up to date information about the chat (current name of the user for one-on-one conversations, current username of a user, group or channel, etc.).
  * Returns a Chat object on success.
  *
  * @param chatId Integer or String Unique identifier for the target chat or username of the target supergroup or channel (in the format @channelusername)
  */
case class GetChat(chatId: ChatId) extends JsonRequest[DetailedChat]

object GetChat {

  implicit val method: Method[GetChat, DetailedChat] =
    new Method[GetChat, DetailedChat] {

      def name: String = "getChat"

      def encoder: Encoder[GetChat] = CirceEncoders.getChatEncoder

      def decoder: Decoder[DetailedChat] = CirceDecoders.detailedChatDecoder

      def uploads(request: GetChat): List[(String, InputFile)] = Nil
    }
}
