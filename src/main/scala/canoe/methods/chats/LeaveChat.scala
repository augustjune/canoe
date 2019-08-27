package canoe.methods.chats

import canoe.marshalling.CirceEncoders
import canoe.methods.Method
import canoe.models.{ChatId, InputFile}
import io.circe.{Decoder, Encoder}

/** Use this method for your bot to leave a group, supergroup or channel. Returns True on success.
  *
  * @param chatId Integer or String Unique identifier for the target chat or username of the target supergroup or channel (in the format @channelusername)
  */
case class LeaveChat(chatId: ChatId)

object LeaveChat {

  implicit val method: Method[LeaveChat, Boolean] =
    new Method[LeaveChat, Boolean] {

      def name: String = "leaveChat"

      def encoder: Encoder[LeaveChat] = CirceEncoders.leaveChatEncoder

      def decoder: Decoder[Boolean] = Decoder.decodeBoolean

      def uploads(request: LeaveChat): List[(String, InputFile)] = Nil
    }
}
