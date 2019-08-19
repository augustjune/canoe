package canoe.models.messages

import canoe.models.Chat

case class ChannelCreated(messageId: Int, chat: Chat, date: Int,
                          channelChatCreated: Boolean) extends TelegramMessage
