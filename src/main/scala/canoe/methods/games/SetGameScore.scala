package canoe.methods.games

import canoe.marshalling.{CirceDecoders, CirceEncoders}
import canoe.methods.{JsonRequest, Method}
import canoe.models.messages.TelegramMessage
import canoe.models.{ChatId, InputFile}
import io.circe.{Decoder, Encoder}

/** Use this method to set the score of the specified user in a game.
  *
  * On success, if the message was sent by the bot, returns the edited Message,
  * otherwise returns True.
  * Returns an error, if the new score is not greater than the user's current
  * score in the chat and force is False.
  *
  * @param userId             Integer Yes User identifier
  * @param score              Integer Yes New score, must be positive
  * @param force              Boolean Optional Pass True, if the high score is allowed to decrease.
  *                           This can be useful when fixing mistakes or banning cheaters
  * @param disableEditMessage Boolean Optional Pass True, if the game message should not be automatically edited to include the current scoreboard
  * @param chatId             Integer or String Optional Required if inline_message_id is not specified.
  *                           Unique identifier for the target chat (or username of the target channel in the format @channelusername)
  * @param messageId          Integer Optional Required if inline_message_id is not specified. Unique identifier of the sent message
  * @param inlineMessageId    String Optional Required if chat_id and message_id are not specified. Identifier of the inline message
  */
case class SetGameScore(userId: Int,
                        score: Long,
                        force: Option[Boolean] = None,
                        disableEditMessage: Option[Boolean] = None,
                        chatId: Option[ChatId] = None,
                        messageId: Option[Int] = None,
                        inlineMessageId: Option[String] = None
                       ) extends JsonRequest[Either[Boolean, TelegramMessage]] {

  if (inlineMessageId.isEmpty) {
    require(chatId.isDefined, "Required if inlineMessageId is not specified")
    require(messageId.isDefined, "Required if inlineMessageId is not specified")
  }

  if (chatId.isEmpty && messageId.isEmpty)
    require(inlineMessageId.isDefined, "Required if chatId and messageId are not specified")
}

object SetGameScore {

  implicit val method: Method[SetGameScore, Either[Boolean, TelegramMessage]] =
    new Method[SetGameScore, Either[Boolean, TelegramMessage]] {

      def name: String = "setGameScore"

      def encoder: Encoder[SetGameScore] = CirceEncoders.setGameScoreEncoder

      def decoder: Decoder[Either[Boolean, TelegramMessage]] =
      // ToDo - set keys
        Decoder.decodeEither("", "")(
          Decoder.decodeBoolean,
          CirceDecoders.telegramMessageDecoder
        )

      def uploads(request: SetGameScore): List[(String, InputFile)] = Nil
    }
}
