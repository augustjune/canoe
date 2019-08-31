package canoe.api.syntax

import canoe.models.Chat
import canoe.models.messages.TelegramMessage


final class ExpectTelegramMessageOps(private val original: ExpectMessage[TelegramMessage]) extends AnyVal {

  def chat: ExpectMessage[Chat] = original andThen (_.chat)

  def date: ExpectMessage[Int] = original andThen (_.date)
}
