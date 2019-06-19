package com.canoe.telegram.models.messages

import com.canoe.telegram.models.Chat

case class ChatPhotoDeleted(messageId: Int, chat: Chat, date: Int,
                            deleteChatPhoto: Boolean) extends TelegramMessage
