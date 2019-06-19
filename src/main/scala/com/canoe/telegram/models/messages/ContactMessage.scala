package com.canoe.telegram.models.messages

import com.canoe.telegram.models.{Chat, Contact, User}

case class ContactMessage(messageId: Int, chat: Chat, date: Int,
                          contact: Contact,
                          from: Option[User] = None,
                          forwardFrom: Option[User] = None,
                          forwardFromChat: Option[Chat] = None,
                          forwardFromMessageId: Option[Int] = None,
                          forwardSignature: Option[String] = None,
                          forwardDate: Option[Int] = None,
                          replyToMessage: Option[TelegramMessage] = None,
                          editDate: Option[Int] = None,
                          authorSignature: Option[String] = None) extends TelegramMessage
