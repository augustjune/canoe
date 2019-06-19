package com.canoe.telegram.models.messages

import com.canoe.telegram.models.{Chat, PhotoSize}

case class ChatPhotoChanged(messageId: Int, chat: Chat, date: Int,
                            newChatPhoto: Seq[PhotoSize]) extends TelegramMessage
