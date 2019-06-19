package com.canoe.telegram.models.messages

import com.canoe.telegram.models.Chat

case class WebsiteConnected(messageId: Int, chat: Chat, date: Int,
                            connectedWebsite: String) extends TelegramMessage
