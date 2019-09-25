package canoe.models.messages

import canoe.models.{Chat, Invoice, User}

final case class InvoiceMessage(messageId: Int,
                                chat: Chat,
                                date: Int,
                                invoice: Invoice,
                                from: Option[User] = None,
                                forwardFrom: Option[User] = None,
                                forwardFromChat: Option[Chat] = None,
                                forwardFromMessageId: Option[Int] = None,
                                forwardSignature: Option[String] = None,
                                forwardSenderName: Option[String] = None,
                                forwardDate: Option[Int] = None,
                                replyToMessage: Option[TelegramMessage] = None,
                                editDate: Option[Int] = None,
                                authorSignature: Option[String] = None)
    extends UserMessage
