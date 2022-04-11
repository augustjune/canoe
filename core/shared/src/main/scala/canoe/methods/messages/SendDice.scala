package canoe.methods.messages

import canoe.marshalling.codecs._
import canoe.methods.Method
import canoe.models.messages.DiceThrownMessage
import canoe.models.{ChatId, DiceEmoji, InputFile, ReplyMarkup}
import io.circe.generic.semiauto
import io.circe.{Decoder, Encoder}

final case class SendDice(chatId: ChatId,
                          emoji: DiceEmoji,
                          disableNotification: Option[Boolean] = None,
                          replyToMessageId: Option[Int] = None,
                          replyMarkup: Option[ReplyMarkup] = None)

object SendDice {
  import io.circe.generic.auto._

  implicit val method: Method[SendDice, DiceThrownMessage] =
    new Method[SendDice, DiceThrownMessage] {

      def name: String = "sendDice"

      def encoder: Encoder[SendDice] = semiauto.deriveEncoder[SendDice].snakeCase

      def decoder: Decoder[DiceThrownMessage] = semiauto.deriveDecoder[DiceThrownMessage]

      def attachments(request: SendDice): List[(String, InputFile)] = Nil
    }
}
