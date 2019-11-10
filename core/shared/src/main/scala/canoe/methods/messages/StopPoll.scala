package canoe.methods.messages

import canoe.marshalling.codecs._
import canoe.methods.Method
import canoe.models.{ChatId, InlineKeyboardMarkup, InputFile, Poll}
import io.circe.generic.semiauto._
import io.circe.{Decoder, Encoder}

/**
  * Use this method to stop a poll which was sent by the bot.
  *
  * On success, the stopped Poll with the final results is returned.
  *
  * @param chatId      Unique identifier for the target chat or username of the target channel
  *                    (in the format @channelusername).
  * @param messageId   Identifier of the original message with the poll
  * @param replyMarkup New inline keyboard.
  */
final case class StopPoll(chatId: ChatId, messageId: Int, replyMarkup: Option[InlineKeyboardMarkup] = None)

object StopPoll {
  import io.circe.generic.auto._

  implicit val method: Method[StopPoll, Poll] =
    new Method[StopPoll, Poll] {

      def name: String = "stopPoll"

      def encoder: Encoder[StopPoll] = deriveEncoder[StopPoll].snakeCase

      def decoder: Decoder[Poll] = deriveDecoder[Poll]

      def attachments(request: StopPoll): List[(String, InputFile)] = Nil
    }
}
