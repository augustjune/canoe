package canoe.methods.chats

import canoe.marshalling.CirceEncoders
import canoe.methods.Method
import canoe.models.{ChatId, InputFile}
import io.circe.{Decoder, Encoder}

/** Use this method to get the number of members in a chat. Returns Int on success.
  *
  * @param chatId Integer or String Unique identifier for the target chat or username of the target supergroup or channel (in the format @channelusername)
  */
case class GetChatMembersCount(chatId: ChatId)

object GetChatMembersCount {

  implicit val method: Method[GetChatMembersCount, Int] =
    new Method[GetChatMembersCount, Int] {

      def name: String = "getChatMembersCount"

      def encoder: Encoder[GetChatMembersCount] = CirceEncoders.getChatMembersCountEncoder

      def decoder: Decoder[Int] = Decoder.decodeInt

      def uploads(request: GetChatMembersCount): List[(String, InputFile)] = Nil
    }
}
