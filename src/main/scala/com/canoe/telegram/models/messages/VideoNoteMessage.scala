package com.canoe.telegram.models.messages

import com.canoe.telegram.models.{Chat, User, VideoNote}

case class VideoNoteMessage(messageId: Int, chat: Chat, date: Int,
                            videoNote: VideoNote,
                            from: Option[User] = None,
                            forwardFrom: Option[User] = None,
                            forwardFromChat: Option[Chat] = None,
                            forwardFromMessageId: Option[Int] = None,
                            forwardSignature: Option[String] = None,
                            forwardDate: Option[Int] = None,
                            replyToMessage: Option[TelegramMessage] = None,
                            editDate: Option[Int] = None,
                            authorSignature: Option[String] = None
                           ) extends TelegramMessage
