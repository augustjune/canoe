package canoe.methods.games

import canoe.marshalling.CirceDecoders
import canoe.marshalling.codecs._
import canoe.methods.Method
import canoe.models.{ChatId, GameHighScore, InputFile}
import io.circe.generic.semiauto.deriveEncoder
import io.circe.{Decoder, Encoder}

/** Use this method to get data for high score tables.
  * Will return the score of the specified user and several of his neighbors in a game.
  * On success, returns an Array of GameHighScore objects.
  *
  * This method will currently return scores for the target user, plus two of his closest neighbors on each side.
  * Will also return the top three users if the user and his neighbors are not among them.
  * Please note that this behavior is subject to change.
  *
  * @param userId          Integer Yes Target user id
  * @param chatId          Integer or String Optional Required if inline_message_id is not specified. Unique identifier for the target chat (or username of the target channel in the format @channelusername)
  * @param messageId       Integer Optional Required if inline_message_id is not specified. Unique identifier of the sent message
  * @param inlineMessageId String Optional Required if chat_id and message_id are not specified. Identifier of the inline message
  */
case class GetGameHighScores(userId: Int,
                             chatId: Option[ChatId] = None,
                             messageId: Option[Int] = None,
                             inlineMessageId: Option[String] = None
                            )

object GetGameHighScores {

  implicit val method: Method[GetGameHighScores, List[GameHighScore]] =
    new Method[GetGameHighScores, List[GameHighScore]] {

      def name: String = "getGameHighScores"

      def encoder: Encoder[GetGameHighScores] = deriveEncoder[GetGameHighScores].snakeCase

      def decoder: Decoder[List[GameHighScore]] = Decoder.decodeList(CirceDecoders.gameHighScoreDecoder)

      def uploads(request: GetGameHighScores): List[(String, InputFile)] = Nil
    }
}
