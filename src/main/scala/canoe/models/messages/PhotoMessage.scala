package canoe.models.messages

import canoe.models.{Chat, MessageEntity, PhotoSize, User}

case class PhotoMessage(messageId: Int, chat: Chat, date: Int,
                        photo: Seq[PhotoSize],
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
                        authorSignature: Option[String] = None) extends TelegramMessage
