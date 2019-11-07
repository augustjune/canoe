package canoe.methods.messages

import canoe.marshalling.codecs._
import canoe.methods.Method
import canoe.models.messages.GameMessage
import canoe.models.{InputFile, ReplyMarkup}
import io.circe.generic.semiauto._
import io.circe.{Decoder, Encoder}

/**
  * Use this method to send a game.
  *
  * On success, the sent Message is returned.
  *
  * @param chatId              Unique identifier for the target chat
  * @param gameShortName       Short name of the game, serves as the unique identifier for the game. Set up your games via Botfather.
  * @param disableNotification Sends the message silently.
  *                            iOS users will not receive a notification,
  *                            Android users will receive a notification with no sound.
  * @param replyToMessageId    If the message is a reply, ID of the original message
  * @param replyMarkup         A JSON-serialized object for an inline keyboard.
  *                            If empty, one 'Play game_title' button will be shown.
  *                            If not empty, the first button must launch the game.
  */
final case class SendGame(chatId: Long,
                          gameShortName: String,
                          disableNotification: Option[Boolean] = None,
                          replyToMessageId: Option[Int] = None,
                          replyMarkup: Option[ReplyMarkup] = None)

object SendGame {
  import io.circe.generic.auto._

  implicit val method: Method[SendGame, GameMessage] =
    new Method[SendGame, GameMessage] {

      def name: String = "sendGame"

      def encoder: Encoder[SendGame] = deriveEncoder[SendGame].snakeCase

      def decoder: Decoder[GameMessage] = deriveDecoder[GameMessage]

      def uploads(request: SendGame): List[(String, InputFile)] = Nil
    }
}
