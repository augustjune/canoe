package com.canoe.telegram.models.messages

import com.canoe.telegram.models.Chat

case class ChannelCreated(messageId: Int, chat: Chat, date: Int,
                          channelChatCreated: Boolean) extends TelegramMessage
