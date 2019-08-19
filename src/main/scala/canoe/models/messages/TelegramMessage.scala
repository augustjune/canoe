package canoe.models.messages

import canoe.models.Chat

trait TelegramMessage {
  def messageId: Int
  def chat: Chat
  def date: Int
}
