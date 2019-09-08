package canoe.syntax

import canoe.models.Chat
import canoe.models.messages.TelegramMessage

final class ExpectTelegramMessageOps(private val original: Expect[TelegramMessage]) extends AnyVal {

  def chat: Expect[Chat] = original.map(_.chat)

  def date: Expect[Int] = original.map(_.date)
}
