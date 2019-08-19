package canoe.models.messages

import canoe.models.Chat

case class WebsiteConnected(messageId: Int, chat: Chat, date: Int,
                            connectedWebsite: String) extends TelegramMessage
