package canoe.methods.messages

import canoe.marshalling.{CirceDecoders, CirceEncoders}
import canoe.methods.{JsonRequest, Method}
import canoe.models.{ChatId, InputFile, Poll, ReplyMarkup}
import io.circe.{Decoder, Encoder}

/**
  * Use this method to stop a poll which was sent by the bot.
  * On success, the stopped Poll with the final results is returned.
  *
  * @param chatId       Unique identifier for the target chat or username of the target channel (in the format @channelusername)
  * @param messageId    Identifier of the original message with the poll
  * @param replyMarkup  A JSON-serialized object for a new message inline keyboard.
  */
case class StopPoll(chatId      : ChatId,
                    messageId   : Int,
                    replyMarkup : Option[ReplyMarkup] = None
                   ) extends JsonRequest[Poll]

object StopPoll {

  implicit val method: Method[StopPoll, Poll] =
    new Method[StopPoll, Poll] {

      def name: String = "stopPoll"

      def encoder: Encoder[StopPoll] = CirceEncoders.stopPollEncoder

      def decoder: Decoder[Poll] = CirceDecoders.pollDecoder

      def uploads(request: StopPoll): List[(String, InputFile)] = Nil
    }
}
