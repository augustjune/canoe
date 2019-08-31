package canoe.methods.chats

import canoe.marshalling.codecs._
import canoe.methods.Method
import canoe.models.{ChatId, ChatMember, InputFile}
import io.circe.generic.semiauto.deriveEncoder
import io.circe.{Decoder, Encoder}

/** Use this method to get a list of administrators in a chat.
  * On success, returns an Array of ChatMember objects that contains information about all chat administrators except other bots.
  * If the chat is a group or a supergroup and no administrators were appointed, only the creator will be returned.
  *
  * @param chatId Integer or String Unique identifier for the target chat or username of the target supergroup or channel (in the format @channelusername)
  */
case class GetChatAdministrators(chatId: ChatId)

object GetChatAdministrators {

  implicit val method: Method[GetChatAdministrators, List[ChatMember]] =
    new Method[GetChatAdministrators, List[ChatMember]] {

      def name: String = "getChatAdministrators"

      def encoder: Encoder[GetChatAdministrators] = deriveEncoder[GetChatAdministrators].snakeCase

      def decoder: Decoder[List[ChatMember]] = Decoder.decodeList

      def uploads(request: GetChatAdministrators): List[(String, InputFile)] = Nil
    }
}
