package com.canoe.telegram.models.messages

import com.canoe.telegram.models.{Chat, MessageEntity, User, Video}

case class VideoMessage(messageId: Int, chat: Chat, date: Int,
                        video: Video,
                        caption: Option[String] = None,
                        captionEntities: Option[Seq[MessageEntity]] = None,
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
