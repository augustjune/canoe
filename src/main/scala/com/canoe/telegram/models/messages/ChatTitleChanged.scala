package com.canoe.telegram.models.messages

import com.canoe.telegram.models.Chat

case class ChatTitleChanged(messageId: Int, chat: Chat, date: Int,
                            newChatTitle: String) extends TelegramMessage
