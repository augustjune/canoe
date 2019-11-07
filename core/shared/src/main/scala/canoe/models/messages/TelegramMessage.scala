package canoe.models.messages

import canoe.models.Chat
import cats.syntax.functor._
import io.circe.Decoder

trait TelegramMessage {
  def messageId: Int
  def chat: Chat
  def date: Int
}

object TelegramMessage {

  implicit val telegramMessageDecoder: Decoder[TelegramMessage] =
    List[Decoder[TelegramMessage]](
      UserMessage.userMessageDecoder.widen,
      SystemMessage.systemMessageDecoder.widen
    ).reduceLeft(_.or(_))
}
