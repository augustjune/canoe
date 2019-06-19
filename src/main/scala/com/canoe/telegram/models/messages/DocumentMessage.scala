package com.canoe.telegram.models.messages

import com.canoe.telegram.models.{Chat, Document, MessageEntity, User}

case class DocumentMessage(messageId: Int, chat: Chat, date: Int,
                           document: Document,
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
