package canoe.methods.games

import canoe.marshalling.codecs._
import canoe.methods.Method
import canoe.models.messages.TelegramMessage
import canoe.models.{ChatId, InputFile}
import io.circe.generic.semiauto.deriveEncoder
import io.circe.{Decoder, Encoder}

/**
  * Use this method to set the score of the specified user in a game.
  *
  * On success, if the message was sent by the bot, returns the edited Message,
  * otherwise returns True.
  * Returns an error, if the new score is not greater than the user's current
  * score in the chat and force is False.
  *
  * Use methods in companion object in order to construct the value of this class.
  *
  * @param userId             User identifier
  * @param score              New score, must be positive
  * @param force              Pass True, if the high score is allowed to decrease.
  *                           This can be useful when fixing mistakes or banning cheaters
  * @param disableEditMessage Pass True, if the game message should not be automatically edited to include the current scoreboard
  * @param chatId             Unique identifier for the target chat (or username of the target channel in the format @channelusername).
  *                           Required if 'inlineMessageId' is not specified.
  * @param messageId          Unique identifier of the sent message.
  *                           Required if 'inlineMessageId' is not specified.
  * @param inlineMessageId    Identifier of the inline message.
  *                           Required if 'chatId' and 'messageId' are not specified.
  */
final case class SetGameScore private (userId: Int,
                                       score: Long,
                                       force: Option[Boolean] = None,
                                       disableEditMessage: Option[Boolean] = None,
                                       chatId: Option[ChatId] = None,
                                       messageId: Option[Int] = None,
                                       inlineMessageId: Option[String] = None)

object SetGameScore {

  /**
    * For the messages sent directed by the bot
    */
  def direct(chatId: ChatId,
             messageId: Int,
             userId: Int,
             score: Long,
             force: Option[Boolean] = None,
             disableEditMessage: Option[Boolean] = None): SetGameScore =
    SetGameScore(userId, score, force, disableEditMessage, Some(chatId), Some(messageId))

  /**
    * For the inlined messages sent via the bot
    */
  def inlined(inlineMessageId: String,
              userId: Int,
              score: Long,
              force: Option[Boolean] = None,
              disableEditMessage: Option[Boolean] = None): SetGameScore =
    SetGameScore(userId, score, force, disableEditMessage, inlineMessageId = Some(inlineMessageId))

  implicit val method: Method[SetGameScore, Either[Boolean, TelegramMessage]] =
    new Method[SetGameScore, Either[Boolean, TelegramMessage]] {

      def name: String = "setGameScore"

      def encoder: Encoder[SetGameScore] = deriveEncoder[SetGameScore].snakeCase

      def decoder: Decoder[Either[Boolean, TelegramMessage]] =
        eitherDecoder(
          Decoder.decodeBoolean,
          TelegramMessage.telegramMessageDecoder
        )

      def uploads(request: SetGameScore): List[(String, InputFile)] = Nil
    }
}
