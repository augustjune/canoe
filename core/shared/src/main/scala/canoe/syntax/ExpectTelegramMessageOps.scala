package canoe.syntax

import canoe.models.Chat
import canoe.models.messages.TelegramMessage

final class ExpectTelegramMessageOps(private val original: Expect[TelegramMessage]) extends AnyVal {

  /**
    * Chat of the originally matched message.
    */
  def chat: Expect[Chat] = original.andThen(_.chat)

  /**
    * Date of the originally matched message.
    */
  def date: Expect[Int] = original.andThen(_.date)
}
