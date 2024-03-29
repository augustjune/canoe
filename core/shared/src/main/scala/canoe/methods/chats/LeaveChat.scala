package canoe.methods.chats

import canoe.marshalling.codecs._
import canoe.methods.Method
import canoe.models.{ChatId, InputFile}
import io.circe.generic.semiauto
import io.circe.{Decoder, Encoder}

/**
  * Use this method for your bot to leave a group, supergroup or channel.
  *
  * Returns True on success.
  *
  * @param chatId Unique identifier for the target chat or username of the target channel
  *               (in the format @channelusername)
  */
final case class LeaveChat(chatId: ChatId)

object LeaveChat {

  implicit val method: Method[LeaveChat, Boolean] =
    new Method[LeaveChat, Boolean] {

      def name: String = "leaveChat"

      def encoder: Encoder[LeaveChat] = semiauto.deriveEncoder[LeaveChat].snakeCase

      def decoder: Decoder[Boolean] = Decoder.decodeBoolean

      def attachments(request: LeaveChat): List[(String, InputFile)] = Nil
    }
}
