package canoe.models.messages

import canoe.models.{Chat, Sticker, User}

case class StickerMessage(messageId: Int,
                          chat: Chat,
                          date: Int,
                          sticker: Sticker,
                          from: Option[User] = None,
                          forwardFrom: Option[User] = None,
                          forwardFromChat: Option[Chat] = None,
                          forwardFromMessageId: Option[Int] = None,
                          forwardSignature: Option[String] = None,
                          forwardDate: Option[Int] = None,
                          replyToMessage: Option[TelegramMessage] = None,
                          editDate: Option[Int] = None,
                          authorSignature: Option[String] = None)
    extends TelegramMessage
