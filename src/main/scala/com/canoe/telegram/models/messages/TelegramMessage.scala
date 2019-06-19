package com.canoe.telegram.models.messages

import com.canoe.telegram.models.Chat

trait TelegramMessage {
  def messageId: Int
  def chat: Chat
  def date: Int
}
